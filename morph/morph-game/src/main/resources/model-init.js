load("nashorn:mozilla_compat.js");
importPackage(Packages.net.carmgate.morph.model)
importPackage(Packages.net.carmgate.morph.model.entities.ship)
importPackage(Packages.net.carmgate.morph.model.entities.physical)
importPackage(Packages.net.carmgate.morph.model.entities)
importPackage(Packages.net.carmgate.morph.model.entities.components.mining)
importPackage(Packages.net.carmgate.morph.model.entities.components.generator)
importPackage(Packages.net.carmgate.morph.model.entities.components.offensive)
importPackage(Packages.net.carmgate.morph.model.entities.components.prop)
importPackage(Packages.net.carmgate.morph.model.entities.components.repair)
importPackage(Packages.net.carmgate.morph.model.geometry)
//importPackage(Packages.net.carmgate.morph.orders)
importPackage(Packages.org.lwjgl.util.vector)
var ArrayList = Java.type('java.util.ArrayList');

me = new Player("Me", Player.PlayerType.PLAYER);
me.color = [0, 1, 0, 1];
world.add(me);
other = new Player("Other", Player.PlayerType.AI);
other.color = [1, 0.2, 0.2, 1];
world.add(other);

asteroid = entityFactory.newInstance(Asteroid.class);
asteroid.getPos().copy(-500, -80);
asteroid.mass = 2000;
asteroid.rotationSpeed = -5;
worldManager.add(asteroid);

ship = entityFactory.newInstance(Ship.class);
ship.getPos().copy(-200, 200);
ship.mass = 1;
ship.durability = 30;
ship.player = me;
ship.energy = 1;
ship.resources = 10;
//ship.getSpeed().copy(-100, -100)
cmp = componentFactory.newInstance(MiningLaser.class);
ship.add(cmp);
cmp = componentFactory.newInstance(SimplePropulsor.class);
ship.add(cmp);
cmp = componentFactory.newInstance(SimpleGenerator.class);
ship.add(cmp);
cmp = componentFactory.newInstance(SimpleRepairer.class);
ship.add(cmp);
cmp = componentFactory.newInstance(Laser.class);
ship.add(cmp);
ship.setSoftSpaceMax(5);
ship.setHardSpaceMax(5);
ship.setXp(5);
shipManager.init(ship);
worldManager.add(ship);

