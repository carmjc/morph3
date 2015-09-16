package net.carmgate.morph.calculator;

import java.util.ArrayList;
import java.util.List;

import net.carmgate.morph.model.entities.physical.ship.Ship;

public class Calculator {

	/**
	 * Dg(n)=\frac{D_{s}}{\sum_{i=1}^{n}(\frac{D_{E_{i}}}{a_{s}-d_{E_{i}}}((\sum_{j=i}^{n}a_{e_{i}})-d_{s})))} (https://www.codecogs.com/latex/eqneditor.php)
	 * <br />
	 * Dg being danger, D being Durability, a being durability loss per second, d being durability loss avoidable per second.<br />
	 * S being self (the defender), E being Ennemies (the attackers)
	 *
	 * This computation can be done in two main ways : with ennemies sorted from the most dangerous ship to the least (minDG(n) or optimized DG, meaning that
	 * the defender eliminates the most dangerous first) or with the reverse (maxDG(n) meaning that the defender eliminates the least dangerous first)<br />
	 *
	 * Using maxDGs allows to see the worst case scenario. Using minDGs allows to see what could happen if the defender uses the best strategy.
	 * @param self
	 * @param ennemies
	 *
	 * @return
	 */
	public float computeDanger(Ship self, List<Ship> ennemies) {
		if (ennemies.size() == 0) {
			return Float.MAX_VALUE;
		}

		List<Ship> tmpList = new ArrayList<>(ennemies);

		float dtsSelf = self.getMaxDefenseDt();
		float atsSelf = self.getMaxDamageDt();
		float totalA = 0;
		for (Ship ennemy : ennemies) {
			float dtsEnnemy = ennemy.getMaxDefenseDt();
			float killTime = ennemy.getDurability() / (atsSelf - dtsEnnemy);
			float totalAtsEnnemy = 0;
			for (Ship tmpEnnemy : tmpList) {
				totalAtsEnnemy += tmpEnnemy.getMaxDamageDt();
			}
			tmpList.remove(ennemy);
			totalA += killTime * Math.max(totalAtsEnnemy - dtsSelf, 0);
		}

		if (totalA == 0) {
			return Float.MAX_VALUE;
		}
		return self.getDurability() / totalA;
	}

}
