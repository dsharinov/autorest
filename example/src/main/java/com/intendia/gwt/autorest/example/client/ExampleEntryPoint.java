package com.intendia.gwt.autorest.example.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.intendia.gwt.autorest.client.CallbackResourceBuilder;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.Consumer;

public class ExampleEntryPoint implements EntryPoint {
    private Consumer<Throwable> err = e -> GWT.log("exception: " + e, e);
    private java.util.function.Consumer<Throwable> onError  = e ->  {
        GWT.log("exception: " + e, e);
        append("exception: "  +e.getMessage());
    };
    //private SwSessionInfo sessionInfo;

    public void onModuleLoad() {
        TextBox name = append(new TextBox());
        HTML out = append(new HTML());

        /*
        ResourceVisitor.Supplier getApi = () -> new RequestResourceBuilder().path(GWT.getModuleBaseURL(), "api");
        ExampleService exampleSrv = new ExampleService_RestServiceModel(() -> getApi.get().header("auth", "ok"));

        Observable.merge(valueChange(name), keyUp(name)).map(e -> name.getValue())
                .switchMap(q -> {
                    Greeting greeting = new Greeting();
                    greeting.greeting = q;
                    return exampleSrv.post(greeting)
                            .map(o -> o.greeting)
                            .onErrorReturn(Throwable::toString);
                })
                .forEach(out::setHTML);
        name.setValue("ping", true);

        append("-- Static tests --");
        exampleSrv.pingObservable().ignoreElements().subscribe(() -> append("observable pong"), err);
        exampleSrv.pingMaybe().ignoreElement().subscribe(() -> append("maybe pong"), err);
        exampleSrv.pingCompletable().subscribe(() -> append("completable pong"), err);

        exampleSrv.getFoo().subscribe(n -> append("observable.foo response: " + n.greeting), err);
        exampleSrv.getFoo("FOO", "BAR", null).subscribe(n -> append("observable.foo response: " + n.greeting), err);
        */

        final ResourceVisitor.Supplier getRest = () -> new CallbackResourceBuilder().path(GWT.getModuleBaseURL(), "rest");

        new SessionResource_RestServiceProxy<Void>(
                getRest::get, v -> append("pinged"), onError).ping();

        new SessionResource_RestServiceProxy<>(
                getRest::get, this::onLogin, onError)
                .login(AuthType.RES_AGENT, "user", "password");

        // error test - getting session info w/o auth token

        new SessionResource_RestServiceProxy<SwSessionInfo>(
                () -> getRest().get(),
                sessionInfo -> append("Empty Token is valid"), onError)
                .getSessionInfo();
    }

    private ResourceVisitor.Supplier getRest() {
        return () -> new CallbackResourceBuilder().path(GWT.getModuleBaseURL(), "rest");
    }

    private void onLogin(SwSessionInfo info) {
        append("User '"+info.userName+"' logged in");
        new SessionResource_RestServiceProxy<SwSessionInfo>(
                () -> getRest().get().header("auth", info.authToken),
                sessionInfo -> append("Token "+sessionInfo.authToken+" is valid"), onError)
                .getSessionInfo();

        new SessionResource_RestServiceProxy<Boolean>(
                () -> getRest().get().header("auth", info.authToken),
                b -> append("Session "+ (b ? " is valid" : " is not valid")), onError)
                .validateSessionInfo(info);

        SwSessionInfo sessionInfo = new SwSessionInfo();
        sessionInfo.authToken = "ZZZ";
        new SessionResource_RestServiceProxy<Boolean>(
                () -> getRest().get().header("auth", info.authToken),
                b -> append("Expired Session "+ (b ? " is valid" : " is not valid")), onError)
                .validateSessionInfo(sessionInfo);
    }

    private static void append(String text) {
        append(new Label(text));
    }

    private static <T extends IsWidget> T append(T w) { RootPanel.get().add(w); return w; }

    private static Observable<KeyUpEvent> keyUp(HasKeyUpHandlers source) {
        return Observable.create(s -> register(s, source.addKeyUpHandler(s::onNext)));
    }

    public static <T> Observable<ValueChangeEvent<T>> valueChange(HasValueChangeHandlers<T> source) {
        return Observable.create(s -> register(s, source.addValueChangeHandler(s::onNext)));
    }

    private static void register(ObservableEmitter<?> s, HandlerRegistration handlerRegistration) {
        s.setCancellable(handlerRegistration::removeHandler);
    }
}
