package com.leaf.yaml.api;

import java.util.Map;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import com.leaf.Leaf;

/**
 * Needed to register a custom tag.
 * 
 * @param <T>
 */
public abstract class ConstructedClass<T> extends AbstractConstruct {

	/**
	 * Prepare a value for deserialization.
	 * 
	 * @param values
	 *            the serialized map
	 * @return the deserialized data
	 */
	public abstract T construct(Map<Object, Object> values);
	
	@Override
	public Object construct(Node node) {
		final Map<Object, Object> values = Leaf.getInstance().getConstructor().constructMap((MappingNode) node);
		return construct(values);
	}
}