package org.apereo.cas.services;

import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.Service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Return only the collection of allowed attributes out of what's resolved
 * for the principal.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@Slf4j
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReturnAllowedAttributeReleasePolicy extends AbstractRegisteredServiceAttributeReleasePolicy {

    private static final long serialVersionUID = -5771481877391140569L;

    private List<String> allowedAttributes = new ArrayList<>();

    @Override
    public Map<String, Object> getAttributesInternal(final Principal principal, final Map<String, Object> attrs,
                                                     final RegisteredService registeredService, final Service selectedService) {
        return authorizeReleaseOfAllowedAttributes(principal, attrs, registeredService, selectedService);
    }

    /**
     * Authorize release of allowed attributes map.
     *
     * @param principal         the principal
     * @param attrs             the attributes
     * @param registeredService the registered service
     * @param selectedService   the selected service
     * @return the map
     */
    protected Map<String, Object> authorizeReleaseOfAllowedAttributes(final Principal principal, final Map<String, Object> attrs,
                                                                      final RegisteredService registeredService, final Service selectedService) {
        val resolvedAttributes = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        resolvedAttributes.putAll(attrs);
        val attributesToRelease = new HashMap<String, Object>();
        getAllowedAttributes()
            .stream()
            .map(attr -> new Object[]{attr, resolvedAttributes.get(attr)})
            .filter(pair -> pair[1] != null)
            .forEach(attribute -> {
                LOGGER.debug("Found attribute [{}] in the list of allowed attributes", attribute[0]);
                attributesToRelease.put((String) attribute[0], attribute[1]);
            });
        return attributesToRelease;
    }

}
