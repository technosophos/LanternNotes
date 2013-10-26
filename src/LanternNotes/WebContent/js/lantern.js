// Bind namespace
var Lantern = Lantern || { settings:{}};

jQuery.extend(Lantern.settings, {
	tinyMCE: {
		//mode: "exact",
		//editor_selector: "fullEditor",
		mode: "exact",
		elements: "editorArea",
		plugins: "fullscreen",
		//add_form_submit_trigger: false, // Handle internally
		theme_advanced_toolbar_location: "top",
		theme_advanced_toolbar_align: "left",
		theme_advanced_buttons3_add: "fullscreen",
		theme: "advanced",
		//invalid_elements: "blink",
	}
});
tinyMCE.init(Lantern.settings.tinyMCE);
/*
$(document).ready(function() {
	if($('#'+Lantern.settings.tinyMCE.elements).size() > 0)
		tinyMCE.init(Lantern.settings.tinyMCE);
});*/