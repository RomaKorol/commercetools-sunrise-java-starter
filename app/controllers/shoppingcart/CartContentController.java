package controllers.shoppingcart;

import com.commercetools.sunrise.framework.components.controllers.PageHeaderControllerComponentSupplier;
import com.commercetools.sunrise.framework.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.framework.controllers.cache.NoCache;
import com.commercetools.sunrise.framework.controllers.metrics.LogMetrics;
import com.commercetools.sunrise.framework.template.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.framework.template.engine.ContentRenderer;
import com.commercetools.sunrise.sessions.cart.CartDiscountCodesExpansionControllerComponent;
import com.commercetools.sunrise.sessions.cart.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.content.SunriseCartContentController;
import com.commercetools.sunrise.shoppingcart.content.viewmodels.CartPageContentFactory;
import com.commercetools.sunrise.wishlist.MiniWishlistControllerComponent;
import com.fasterxml.jackson.databind.JsonNode;
import io.sphere.sdk.carts.Cart;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@LogMetrics
@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class,
        CartOperationsControllerComponentSupplier.class,
        CartDiscountCodesExpansionControllerComponent.class,
        MiniWishlistControllerComponent.class
})
public final class CartContentController extends SunriseCartContentController {

    @Inject
    public CartContentController(final ContentRenderer contentRenderer,
                                 final CartFinder cartFinder,
                                 final CartPageContentFactory pageContentFactory) {
        super(contentRenderer, cartFinder, pageContentFactory);
    }

    public CompletionStage<Result> getCurrentCart() {
        return getCartFinder().get().thenComposeAsync(
                cartOpt -> cartOpt.map(this::handleCurrentCart).orElse(handleCartNotFound()));
    }

    private CompletionStage<Result> handleCartNotFound() {
        return supplyAsync(() -> {
            return notFound("Curt not found");
        });
    }

    private CompletionStage<Result> handleCurrentCart(Cart cart) {
        return supplyAsync(() -> {
            JsonNode jsonNode = Json.toJson(cart);
            return ok(jsonNode);
        });
    }

    @Override
    public String getTemplateName() {
        return "cart";
    }

    @Override
    public String getCmsPageKey() {
        return "default";
    }
}
