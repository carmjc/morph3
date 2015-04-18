package net.carmgate.morph.model.orders;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrderFactory {

	@Inject
	private Instance<Order> orders;

	@SuppressWarnings("unchecked")
	public <U extends Order> U createOrder(OrderType orderType) {
		return (U) orders.select(orderType.getClazz()).get();
	}
}
