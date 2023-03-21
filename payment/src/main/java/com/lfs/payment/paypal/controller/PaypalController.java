package com.lfs.payment.paypal.controller;

import com.lfs.payment.paypal.service.PaypalService;
import com.lfs.payment.paypal.utils.URLUtils;
import com.paypal.api.payments.Capture;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/paypal")
@Slf4j
public class PaypalController {

	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";


	@Autowired
	private PaypalService paypalService;

	@GetMapping("/index")
	public String index(){
		return "paypal/index";
	}

	@PostMapping("/pay")
	public String pay(HttpServletRequest request){
		//定义支付成功和取消支付跳转url
		String cancelUrl = URLUtils.getBaseURl(request) + "/paypal/" + CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/paypal/" + SUCCESS_URL;
		String invoiceNumber="123456abc";//自定义一个账单号
		try {
			Payment payment = paypalService.createPayment(
					"100.01",//支付金额
					"USD",//金额的币种
					"订单描述",
					cancelUrl,
					successUrl,invoiceNumber);
			log.info("payment id:"+payment.getId());
			log.info("payment state:"+payment.getState());
			log.info("payment invoiceNumber:"+payment.getTransactions().get(0).getInvoiceNumber());
			//这里成功返回payment对象，表示PayPal那边已经成功创建订单。
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					log.info("create payment success");
					return "redirect:" + links.getHref();//把买家重定向到支付页面
				}
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}

	@RequestMapping(method = RequestMethod.GET, value = CANCEL_URL)
	public String cancelPayment(){
		return "paypal/cancel";
	}

	@RequestMapping(method = RequestMethod.GET, value = SUCCESS_URL)
	public String successPayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
		try {
			log.info("start execute payment.");
			Payment payment = paypalService.executePayment(paymentId, payerId);//买家授权支付
			log.info("payment id:"+payment.getId());
			log.info("payment state:"+payment.getState());
			// 订单总价
			String total = payment.getTransactions().get(0).getAmount().getTotal();
			log.info("payment total:"+total);
			String orderId = payment.getTransactions().get(0).getRelatedResources().get(0).getOrder().getId();
			log.info("payment invoiceNumber:"+payment.getTransactions().get(0).getInvoiceNumber());
			log.info("payment orderId:"+orderId);
			log.info("end execute payment.");
			log.info("start capture payment.");
			Capture capture=paypalService.capturePayment(total,payment.getTransactions().get(0).getAmount().getCurrency(),orderId);//捕获到买家授权的订单，整个过程到这里就完成了。
			log.info("Capture id = " + capture.getId() + " and status = " + capture.getState());
			if(payment.getState().equals("completed")){
				return "paypal/success";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}

}
