package org.example;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Theme("my-theme")
@PWA(name = "Osano Test", shortName = "OsanoTest")
public class Application implements AppShellConfigurator {
}
