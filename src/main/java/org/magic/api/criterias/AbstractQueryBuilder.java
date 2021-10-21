package org.magic.api.criterias;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SuppressWarnings("unchecked")
public abstract class AbstractQueryBuilder<T> implements MTGQueryBuilder<T> {

	protected Map<Class, MTGCriteriaConverter> registry;

	
	protected AbstractQueryBuilder() {
		registry = new HashMap<>();
	}
	
	@Override
	public <U> void addConvertor(Class<U> clazz, MTGCriteriaConverter<U> mtgCriteriaConverter) {
		registry.put(clazz, mtgCriteriaConverter);
	}
	
	
	@Override
	public T build(List<MTGCrit> crits) {
		return build(crits.stream().toArray(MTGCrit[]::new));
	}
	
	@Override
	public <U> Object getValueFor(U object)
	{
		if(registry.get(object.getClass())!=null)
			return registry.get(object.getClass()).marshal(object);
		
		else
			return object;
		
	}
	
	public <U> Collection<Object> getValueFor(Collection<U> object)
	{
		return object.stream().map(o->registry.get(o.getClass()).marshal(o)).collect(Collectors.toList());
	}
	
	public <U> Collection<Object> getValueFor(U[] objects)
	{
		return getValueFor(Arrays.asList(objects));
	}
	
	
}
