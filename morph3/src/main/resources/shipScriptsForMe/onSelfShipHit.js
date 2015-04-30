if (self.getActionOrder().getOrderType().name() != "ATTACK") {
	attack = orderFactory.newInstance("ATTACK", self);
	attack.setTarget(aggressor);
	self.add(attack);
}
if (self.integrity < 1) {
	repair = orderFactory.newInstance("REPAIR_SELF", self);
	self.add(repair);
}
