package com.lfs.payment.paypal.service;

import com.lfs.payment.paypal.constant.Constant;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class PaypalService {



	@Autowired
	private APIContext apiContext;

	public Payment createPayment(
			String total,
			String currency,
			String description,
			String cancelUrl,
			String successUrl,String invoiceNumber) throws PayPalRESTException {
		Amount amount = new Amount();
		amount.setCurrency(currency);
		amount.setTotal(total);
		Details details=new Details();
		details.setSubtotal(total);
		amount.setDetails(details);
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		transaction.setInvoiceNumber(invoiceNumber);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod(Constant.PAYMENT_METHOD_PAYPAL);

		Payment payment = new Payment();
		payment.setIntent(Constant.PAYMENT_INTENT_ORDER);
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		return payment.create(apiContext);
	}

	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

	public Capture capturePayment(String total,String currency,String orderId){
		try{
			Amount amount = new Amount();
			amount.setCurrency(currency);
			amount.setTotal(total);

			// Authorize order
			Order order = new Order();
			order = Order.get(apiContext, orderId);
			order.setAmount(amount);
			Authorization authorization = order.authorize(apiContext);

			// Capture payment
			Capture capture = new Capture();
			capture.setAmount(amount);
			capture.setIsFinalCapture(true);

			Capture responseCapture = authorization.capture(apiContext, capture);
			return responseCapture;

		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
			return null;
		}
	}

}
