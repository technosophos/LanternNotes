/**
 * Main Sinciput javascript library.
 * This requres the jQuery library.
 * @author M Butcher
 */
 
// Bind namespace
var Sinciput = Sinciput || { settings:{}, sections:{}, c:{} };

// General utilities:
jQuery.extend( Sinciput, {

	showLogin: function() {
		$("#loginPane").show("fast");
		$("#loginPane > form").submit(Sinciput.doLogin);
	},

	doLogin: function(myform) {
		$("#loginPane").hide("fast");
		myForm.submit();
	},

});

jQuery.extend(Sinciput.settings, {
	tinyMCE: {
		//mode: "exact",
		//editor_selector: "fullEditor",
		mode: "none",
		plugins: "fullscreen",
		add_form_submit_trigger: false, // Handle internally
		theme_advanced_toolbar_location: "top",
		theme_advanced_toolbar_align: "left",
		theme_advanced_buttons3_add: "fullscreen",
		theme: "advanced",
		//invalid_elements: "blink",
	}
});

// Sections:
jQuery.extend( Sinciput.sections, {

	_ss: Sinciput.settings,
	
	/**
	 * Used to pass a click event handler to jQuery.
	 */
	anchorClickHandler: function() {
		//alert("Painting subsection.");
		return Sinciput.sections.paintSubsection( $(this).eq(0).attr("href") );
	},
	
	/**
	 * jQuery callback to prepare a subsection
	 */	
	prepareSubsection: function() {
		// Paint editor if necessary
		var b = $("#body");
		if(b.length == 1) {
				tinyMCE.idCounter = 0; // Manually reset ID counter
				tinyMCE.execCommand("mceAddControl", true, "body");
				b.parents("form").eq(0).submit(function(){
					b.get(0).value = tinyMCE.getContent(); // Hack to set content
					//alert("Submitting " + b.text());
					return true;
				});
		}
		// Add event handlers to links		
		$("#bodyPane a[href]").each(function() {
			// Scan links, adding event handler where necessary.
			if(this.href.indexOf(Sinciput.settings.app_url) > -1) //alert(this.href);
				$(this).click(Sinciput.sections.anchorClickHandler);
		});
	},
	
	/**
	 * Paint a new section. 
	 */
	paintSection: function(title, contentURI, menu) {
		title = title || "Unknown";
		menu = menu || {"Home":""};
		contentURI = contentURI || "";
		var menuItemsStr = this.menuItems(menu);
		$("#navPaneTitle h2.navTitle").text(title);
		$("#navPaneMenu ul").slideUp('slow', function(){ $(this).html(menuItemsStr).slideDown('slow')});
		$("head > title").text(this.title);
		var loadURL = this._ss.absolute_uri + '/'+ contentURI;
		//alert(loadURL);
		$("#bodyPane").html("<strong>Loading...</strong>").load(loadURL, Sinciput.sections.prepareSubsection);
		return true;
	},
	
	/**
	 * Paint a subsection only.
	 */
	paintSubsection: function(loadURL) {
		//var loadURL = Sinciput.settings.absolute_uri + '/'+ contentURI;
		if($("#body").length == 1)			
			tinyMCE.execCommand("mceRemoveControl", true, "body");
		/*$("#bodyPane").html("<strong>Loading...</strong>").load(loadURL, function() {
			if($("#body").length == 1)
				tinyMCE.execCommand("mceAddControl", true, "body");
				
			// Probably should not use an anonymous function here.
			$("#bodyPane a[href]").click(Sinciput.sections.anchorClickHandler);
		});*/
		$("#bodyPane").html("<strong>Loading...</strong>").load(loadURL, Sinciput.sections.prepareSubsection );
		return false;
	},
	
	menuItems: function( obj ) {
		var buf = "";
		var f = '<li><a onclick="return Sinciput.sections.paintSubsection(this.href)" href="' 
				+ Sinciput.settings.absolute_uri +'/';
		for (var k in obj ) {
			buf = buf.concat(f ,obj[k],'">' + k + '</a></li>');
		}
		//alert(buf);
		return buf;
	},
	
	paintCourses: function() {
		return this.paintSection("Courses", 'list_courses', {
			"List Courses": "list_courses",
			"Add Class": "add_course", 
		}); 
	},
	
	paintResources: function() {
		return this.paintSection("Resources",  'list_sources',{
			"List Resources": "list_sources",
			"Lookup Resources": "lookup_source",
			"Add Resource": "add_source", 
		}); 
	},
	
	paintJournals: function() {
		return this.paintSection("Journals", 'list_journals',{
			"List Journals": "list_journals",
			"Add Journal": "add_journal", 
		}); 
	},
});