package net.carmgate.morph.eventmgt;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.reflections.Reflections;

@Singleton
public class EventManager {

	private final Map<Class<?>, Set<Method>> observingMethodsMapByOwnerClass = new HashMap<>();
	private final Map<Class<?>, Set<Method>> observingMethodsMapByEvent = new HashMap<>();

	public void scan(Object o) {

		// Look for @MObserves
		Set<Method> observingMethods = observingMethodsMapByOwnerClass.get(o.getClass());
		if (observingMethods == null) {
			observingMethods = new Reflections(o).getMethodsWithAnyParamAnnotated(MObserves.class);
			observingMethodsMapByOwnerClass.put(o.getClass(), observingMethods);

			observingMethods.forEach(m -> {
				m.getAnnotatedParameterTypes()
			});
		}
	}
}
