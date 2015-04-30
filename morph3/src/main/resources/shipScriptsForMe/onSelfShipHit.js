if (self.getActionOrder().getOrderType().name() != "ATTACK") {
	attack = orderFactory.newInstance("ATTACK", self);
	attack.setTarget(aggressor);
	self.add(attack);
}