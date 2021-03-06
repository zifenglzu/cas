package org.apereo.cas.support.oauth.web;

import org.apereo.cas.support.oauth.OAuth20Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jasig.cas.client.util.URIBuilder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;

import java.util.Optional;

/**
 * This is {@link OAuth20CasCallbackUrlResolver}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth20CasCallbackUrlResolver implements UrlResolver {
    private final String callbackUrl;

    private static Optional<URIBuilder.BasicNameValuePair> getQueryParameter(final WebContext context, final String name) {
        val builderContext = new URIBuilder(context.getFullRequestURL());
        return builderContext.getQueryParams()
            .stream().filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Override
    public String compute(final String url, final WebContext context) {
        if (url.startsWith(callbackUrl)) {
            val builder = new URIBuilder(url, true);

            var parameter = getQueryParameter(context, OAuth20Constants.CLIENT_ID);
            parameter.ifPresent(basicNameValuePair -> builder.addParameter(basicNameValuePair.getName(), basicNameValuePair.getValue()));

            parameter = getQueryParameter(context, OAuth20Constants.REDIRECT_URI);
            parameter.ifPresent(basicNameValuePair -> builder.addParameter(basicNameValuePair.getName(), basicNameValuePair.getValue()));

            parameter = getQueryParameter(context, OAuth20Constants.ACR_VALUES);
            parameter.ifPresent(basicNameValuePair -> builder.addParameter(basicNameValuePair.getName(), basicNameValuePair.getValue()));

            parameter = getQueryParameter(context, OAuth20Constants.RESPONSE_TYPE);
            parameter.ifPresent(basicNameValuePair -> builder.addParameter(basicNameValuePair.getName(), basicNameValuePair.getValue()));

            parameter = getQueryParameter(context, OAuth20Constants.GRANT_TYPE);
            parameter.ifPresent(basicNameValuePair -> builder.addParameter(basicNameValuePair.getName(), basicNameValuePair.getValue()));

            val callbackResolved = builder.build().toString();

            LOGGER.debug("Final resolved callback URL is [{}]", callbackResolved);
            return callbackResolved;
        }
        return url;
    }
}
