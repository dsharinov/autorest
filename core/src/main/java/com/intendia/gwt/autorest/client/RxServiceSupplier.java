package com.intendia.gwt.autorest.client;

/**
 * @author DimaS
 */
@FunctionalInterface
public interface RxServiceSupplier {
    RestServiceModel create(ResourceVisitor.Supplier parent);
}
