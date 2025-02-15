package com.jetbrains.edu.learning.checkio.connectors;

import com.jetbrains.edu.learning.authUtils.OAuthRestService;
import com.jetbrains.edu.learning.checkio.utils.CheckiONames;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.ide.RestService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CheckiOOAuthRestService extends OAuthRestService {
  private final Pattern myOAuthCodePattern;
  private final CheckiOOAuthConnector myOAuthConnector;

  protected CheckiOOAuthRestService(
    @NotNull String platformName,
    @NotNull CheckiOOAuthConnector oauthConnector
  ) {
    super(platformName);
    myOAuthCodePattern = oauthConnector.getOAuthPattern();
    myOAuthConnector = oauthConnector;
  }

  @Override
  protected @NotNull String getServiceName() {
    return myOAuthConnector.getServiceName();
  }

  @Override
  protected boolean isHostTrusted(@NotNull FullHttpRequest request,
                                  @NotNull QueryStringDecoder urlDecoder) throws InterruptedException, InvocationTargetException {
    final String uri = request.uri();
    final Matcher codeMatcher = myOAuthCodePattern.matcher(uri);
    if (request.method() == HttpMethod.GET && codeMatcher.matches()) {
      return true;
    }
    return super.isHostTrusted(request, urlDecoder);
  }

  @Nullable
  @Override
  public String execute(
    @NotNull QueryStringDecoder decoder,
    @NotNull FullHttpRequest request,
    @NotNull ChannelHandlerContext context
  ) throws IOException {
    final String uri = decoder.uri();
    LOG.info("Request: " + uri);

    if (myOAuthCodePattern.matcher(uri).matches()) {
      final String code = getStringParameter(CODE_ARGUMENT, decoder);
      assert code != null; // cannot be null because of pattern

      LOG.info(myPlatformName + ": OAuth code is handled");
      final boolean success = myOAuthConnector.login(code);

      if (success) {
        return sendOkResponse(request, context);
      }
      else {
        final String errorMessage = "Failed to login to " + CheckiONames.CHECKIO;
        return sendErrorResponse(request, context, errorMessage);
      }
    }

    RestService.sendStatus(HttpResponseStatus.BAD_REQUEST, false, context.channel());
    return "Unknown command: " + uri;
  }
}
