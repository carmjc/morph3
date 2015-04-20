package net.carmgate.morph.model.orders;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.eventmgt.MEventManager;

@Singleton
public class OrderFactory {

	@Inject private Instance<Order> orders;
	@Inject private MEventManager eventManager;

	@SuppressWarnings("unchecked")
	public <U extends Order> U newInstance(OrderType orderType) {
		final U u = (U) orders.select(orderType.getClazz()).get();
		eventManager.scanAndRegister(u);
		return u;
	}
}
