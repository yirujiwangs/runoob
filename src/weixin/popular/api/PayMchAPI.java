package weixin.popular.api;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import utils.common.WechatConstansUtil;
import weixin.popular.bean.paymch.*;
import weixin.popular.client.LocalHttpClient;
import weixin.popular.util.MapUtil;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 微信支付 基于V3.X 版本
 * 
 * @author Yi
 *
 */
public class PayMchAPI extends BaseAPI {

	/**
	 * 统一下单 请使用 payUnifiedorder(Unifiedorder unifiedorder,String key), 自动生成sign
	 * 
	 * @param unifiedorder
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Deprecated
	public static UnifiedorderResult payUnifiedorder(Unifiedorder unifiedorder)
			throws UnsupportedEncodingException {
		return payUnifiedorder(unifiedorder, null);
	}

	/**
	 * 统一下单
	 * 
	 * @param unifiedorder
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static UnifiedorderResult payUnifiedorder(Unifiedorder unifiedorder,
			String key) throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(unifiedorder);
		if (key != null) {
			String sign = SignatureUtil.generateSign(map, key);
			unifiedorder.setSign(sign);
		}


		System.out.println(JSONObject.toJSONString(unifiedorder));

		String unifiedorderXML = XMLConverUtil.convertToXML(unifiedorder);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/unifiedorder")
				.setEntity(new StringEntity(unifiedorderXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				UnifiedorderResult.class);
	}

	/**
	 * 刷卡支付 提交被扫支付API
	 * 
	 * @param micropay
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MicropayResult payMicropay(Micropay micropay, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(micropay);
		String sign = SignatureUtil.generateSign(map, key);
		micropay.setSign(sign);
		String closeorderXML = XMLConverUtil.convertToXML(micropay);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/micropay")
				.setEntity(new StringEntity(closeorderXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				MicropayResult.class);
	}

	/**
	 * 查询订单
	 * 
	 * @param mchOrderquery
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MchOrderInfoResult payOrderquery(MchOrderquery mchOrderquery,
			String key) throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(mchOrderquery);
		String sign = SignatureUtil.generateSign(map, key);
		mchOrderquery.setSign(sign);
		String closeorderXML = XMLConverUtil.convertToXML(mchOrderquery);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/orderquery")
				.setEntity(new StringEntity(closeorderXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				MchOrderInfoResult.class);
	}

	/**
	 * 关闭订单
	 * 
	 * @param closeorder
	 * @param key
	 *            商户支付密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MchBaseResult payCloseorder(Closeorder closeorder, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(closeorder);
		String sign = SignatureUtil.generateSign(map, key);
		closeorder.setSign(sign);
		String closeorderXML = XMLConverUtil.convertToXML(closeorder);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/closeorder")
				.setEntity(new StringEntity(closeorderXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				MchBaseResult.class);
	}

	/**
	 * 申请退款
	 *
	 * 注意： 1.交易时间超过半年的订单无法提交退款；
	 * 2.微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号
	 * 。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额。
	 * 
	 * @param secapiPayRefund
	 * @param key
	 *            商户支付密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SecapiPayRefundResult secapiPayRefund(
			SecapiPayRefund secapiPayRefund, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(secapiPayRefund);
		String sign = SignatureUtil.generateSign(map, key);
		secapiPayRefund.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML(secapiPayRefund);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/secapi/pay/refund")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.keyStoreExecuteXmlResult(
				secapiPayRefund.getMch_id(), httpUriRequest,
				SecapiPayRefundResult.class);
	}

	/**
	 * 撤销订单 7天以内的交易单可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API。提交支付交易后调用【查询订单API】，
	 * 没有明确的支付结果再调用【撤销订单API】。
	 * 
	 * @param mchReverse
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MchReverseResult secapiPayReverse(MchReverse mchReverse,
			String key) throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(mchReverse);
		String sign = SignatureUtil.generateSign(map, key);
		mchReverse.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML(mchReverse);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/secapi/pay/reverse")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.keyStoreExecuteXmlResult(mchReverse.getMch_id(),
				httpUriRequest, MchReverseResult.class);
	}

	/**
	 * 查询退款
	 *
	 * 提交退款申请后，通过调用该接口查询退款状态。退款有一定延时，用零钱支付的退款 20 分钟内到账，银行卡支付的退款3 个工作日后重新查询退款状态。
	 * 
	 * @param refundquery
	 * @param key
	 *            商户支付密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static RefundqueryResult payRefundquery(Refundquery refundquery,
			String key) throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(refundquery);
		String sign = SignatureUtil.generateSign(map, key);
		refundquery.setSign(sign);
		String refundqueryXML = XMLConverUtil.convertToXML(refundquery);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/refundqueryd")
				.setEntity(new StringEntity(refundqueryXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				RefundqueryResult.class);
	}

	/**
	 * 下载对账单
	 * 
	 * @param downloadbill
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static DownloadbillResult payDownloadbill(
			MchDownloadbill downloadbill, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(downloadbill);
		String sign = SignatureUtil.generateSign(map, key);
		downloadbill.setSign(sign);
		String closeorderXML = XMLConverUtil.convertToXML(downloadbill);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/pay/downloadbill")
				.setEntity(new StringEntity(closeorderXML, "utf-8")).build();
		return LocalHttpClient.execute(httpUriRequest,
				new ResponseHandler<DownloadbillResult>() {

					@Override
					public DownloadbillResult handleResponse(
							HttpResponse response)
							throws ClientProtocolException, IOException {
						int status = response.getStatusLine().getStatusCode();
						if (status >= 200 && status < 300) {
							HttpEntity entity = response.getEntity();
							String str = EntityUtils.toString(entity, "utf-8");
							if (str.startsWith("<xml>")) {
								return XMLConverUtil.convertToObject(
										DownloadbillResult.class, str);
							} else {
								DownloadbillResult dr = new DownloadbillResult();
								dr.setData(str);
								return dr;
							}
						} else {
							throw new ClientProtocolException(
									"Unexpected response status: " + status);
						}
					}
				});
	}

	/**
	 * 短链接转换
	 * 
	 * @param shorturl
	 * @param key
	 *            商户支付密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MchShorturlResult toolsShorturl(MchShorturl shorturl,
			String key) throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(shorturl);
		String sign = SignatureUtil.generateSign(map, key);
		shorturl.setSign(sign);
		String shorturlXML = XMLConverUtil.convertToXML(shorturl);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/tools/shorturl")
				.setEntity(new StringEntity(shorturlXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				MchShorturlResult.class);
	}

	/**
	 * 测速上报
	 * 
	 * @param report
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static MchBaseResult payitilReport(Report report, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(report);
		String sign = SignatureUtil.generateSign(map, key);
		report.setSign(sign);
		String shorturlXML = XMLConverUtil.convertToXML(report);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader).setUri(MCH_URI + "/payitil/report")
				.setEntity(new StringEntity(shorturlXML, "utf-8")).build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				MchBaseResult.class);
	}

	/**
	 * 发放代金券
	 * 
	 * @param sendCoupon
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SendCouponResult mmpaymkttransfersSend_coupon(
			SendCoupon sendCoupon, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(sendCoupon);
		String sign = SignatureUtil.generateSign(map, key);
		sendCoupon.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML(sendCoupon);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/mmpaymkttransfers/send_coupon")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.keyStoreExecuteXmlResult(sendCoupon.getMch_id(),
				httpUriRequest, SendCouponResult.class);
	}

	/**
	 * 查询代金券批次
	 * 
	 * @param queryCouponStock
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static QueryCouponStockResult mmpaymkttransfersQuery_coupon_stock(
			QueryCouponStock queryCouponStock, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(queryCouponStock);
		String sign = SignatureUtil.generateSign(map, key);
		queryCouponStock.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil
				.convertToXML(queryCouponStock);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/mmpaymkttransfers/query_coupon_stock")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				QueryCouponStockResult.class);
	}

	/**
	 * 查询代金券信息
	 * 
	 * @param queryCoupon
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static QueryCouponResult promotionQuery_coupon(
			QueryCoupon queryCoupon, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(queryCoupon);
		String sign = SignatureUtil.generateSign(map, key);
		queryCoupon.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML(queryCoupon);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/promotion/query_coupon")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.executeXmlResult(httpUriRequest,
				QueryCouponResult.class);
	}

	/**
	 * 现金红包
	 *
	 * 微信红包发送规则 1. 发送频率规则 　 * 每分钟发送红包数量不得超过1800个； 　 *
	 * 北京时间0：00-8：00不触发红包赠送；（如果以上规则不满足您的需求，请发邮件至wxhongbao@tencent.com获取升级指引） 2.
	 * 红包规则 单个红包金额介于[1.00元，200.00元]之间；
	 * 同一个红包只能发送给一个用户；（如果以上规则不满足您的需求，请发邮件至wxhongbao@tencent.com获取升级指引）
	 * 
	 * @param sendredpack
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SendredpackResult mmpaymkttransfersSendredpack(
			Sendredpack sendredpack, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(sendredpack);
		System.out.println(map);
		String sign = SignatureUtil.generateSign(map, key);
		sendredpack.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML(sendredpack);
		System.out.println(secapiPayRefundXML);
		System.out.println("DSAD==" + WechatConstansUtil.certLocation);
		LocalHttpClient.initMchKeyStore(sendredpack.getMch_id(),
				WechatConstansUtil.certLocation);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/mmpaymkttransfers/sendredpack")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		System.out.println("build" + sendredpack.getMch_id());
		return LocalHttpClient.keyStoreExecuteXmlResult(
				sendredpack.getMch_id(), httpUriRequest,
				SendredpackResult.class);
	}

	/**
	 * 裂变红包
	 * 
	 * @param sendgroupredpack
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static SendredpackResult mmpaymkttransfersSendgroupredpack(
			Sendgroupredpack sendgroupredpack, String key)
			throws UnsupportedEncodingException {
		Map<String, String> map = MapUtil.objectToMap(sendgroupredpack);
		String sign = SignatureUtil.generateSign(map, key);
		sendgroupredpack.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil
				.convertToXML(sendgroupredpack);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/mmpaymkttransfers/sendgroupredpack")
				.setEntity(new StringEntity(secapiPayRefundXML, "utf-8"))
				.build();
		return LocalHttpClient.keyStoreExecuteXmlResult(
				sendgroupredpack.getMch_id(), httpUriRequest,
				SendredpackResult.class);
	}

	/**
	 * 企业付款
	 * 
	 * @param transfers
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static TransfersResult mmpaymkttransfersPromotionTransfers(Transfers transfers,String key) throws UnsupportedEncodingException{
		Map<String,String> map = MapUtil.objectToMap( transfers);
		String sign = SignatureUtil.generateSign(map,key);
		transfers.setSign(sign);
		String secapiPayRefundXML = XMLConverUtil.convertToXML( transfers);
	//	LocalHttpClient.initMchKeyStore(transfers.getMchid(),WechatConstansUtil.certLocation);
		String str = "F:\\apiclient_cert.p12";
		LocalHttpClient.initMchKeyStore(transfers.getMchid(),str);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(xmlHeader)
				.setUri(MCH_URI + "/mmpaymkttransfers/promotion/transfers")
				.setEntity(new StringEntity(secapiPayRefundXML,"utf-8"))
				.build();
		return LocalHttpClient.keyStoreExecuteXmlResult(transfers.getMchid(),httpUriRequest,TransfersResult.class);
	}
}
