package com.technosophos.lantern.commands.template;

import org.apache.velocity.VelocityContext;
import com.technosophos.lantern.util.TemplateTools;

import com.technosophos.rhizome.command.template.DoVelocityTemplate;

/**
 * This extends the features of the Rhizome {@link DoVelocityTemplate} class.
 * Specifically, it adds the 'tpl' template tools (See {@link TemplateTools}).
 * @author mbutcher
 *
 */
public class DoLanternVelocityTemplate extends DoVelocityTemplate {
	protected VelocityContext createContext() {
		VelocityContext cxt = super.createContext();
		TemplateTools tt = new TemplateTools();
		cxt.put("tpl", tt);
		return cxt;
	}
}
