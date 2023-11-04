package org.example.views.about;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.example.views.MainLayout;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

	public AboutView() {
		this.setSpacing(false);

		H2 header = new H2("How To Use");
		header.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
		this.add(header, new Paragraph("Basically; once you select a Currency to lookup from the dropdown; you're good to go! :D"));

		this.setSizeFull();
		this.setJustifyContentMode(JustifyContentMode.CENTER);
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		this.getStyle().set("text-align", "center");
	}
}