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
me.color = [1, 0.5, 0.5, 1];
world.add(me);
other = new Player("Other", Player.PlayerType.AI);
other.color = [0.2, 1, 0.5, 1];
world.add(other);

asteroid = entityFactory.newInstance(Asteroid.class);
asteroid.getPos().copy(-500, -80);
asteroid.mass = 2000;
asteroid.rotateSpeed = 5;
worldManager.add(asteroid);

ship = entityFactory.newInstance(Ship.class);
ship.getPos().copy(-200, 200);
ship.mass = 0.5;
ship.durability = 30;
ship.player = me;
ship.energy = 1;
ship.resources = 10;
//ship.getSpeed().copy(-100, -100)
cmp = componentFactory.newInstance(MiningLaser.class);
componentManager.init(cmp);
ship.add(cmp);
cmp = componentFactory.newInstance(SimplePropulsor.class);
componentManager.init(cmp);
ship.add(cmp);
cmp = componentFactory.newInstance(SimpleGenerator.class);
componentManager.init(cmp);
ship.add(cmp);
cmp = componentFactory.newInstance(SimpleRepairer.class);
componentManager.init(cmp);
ship.add(cmp);
cmp = componentFactory.newInstance(Laser.class);
componentManager.init(cmp);
ship.add(cmp);
ship.setSoftSpaceMax(5);
ship.setHardSpaceMax(5);
ship.setXp(5);
shipManager.create(ship);
worldManager.add(ship);

