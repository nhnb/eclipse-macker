/**
 * 
 */
package de.his.core.tools.cs.sys.quality.eclipsemacker.custommacker;

import java.util.ArrayList;
import java.util.HashMap;

import net.innig.macker.event.AccessRuleViolation;
import net.innig.macker.event.ListenerException;
import net.innig.macker.event.MackerEvent;
import net.innig.macker.event.MackerEventListener;
import net.innig.macker.event.MackerIsMadException;
import net.innig.macker.rule.RuleSet;

/**
 * Erweiterter MackerEventListener, speichert Macker Regleverstoesse in einer
 * Map.
 * 
 * @author Bender
 */
public class MackerListener implements  MackerEventListener {

	/**
	 * Die violation Map speichert als Key den Urpsrung des Fehlers d.h. den PaketPfad einer
	 * Class Datei. Als Value wird eine Liste mit allen gefundene Regelverstoessen gespeichert.
	 */
	private HashMap<String, ArrayList<AccessRuleViolation>> violation;
	

    /**
     * Create a new macker listener 
     */
	public MackerListener() {
		this.violation = new HashMap<String, ArrayList<AccessRuleViolation>>();
		
	}
	
    /**
     * Handler speichert die geworfenen und relevanten Events in einer Map@Override
     */
    @Override
    public void handleMackerEvent(RuleSet ruleSet, MackerEvent event) {
		 if (event instanceof AccessRuleViolation) {
			 AccessRuleViolation e = (AccessRuleViolation) event;
			 
			 if (violation.get(e.getFrom().toString()) == null) {
				 violation.put(e.getFrom().toString(), new ArrayList<AccessRuleViolation>()); 
			 } 
			 violation.get(e.getFrom().toString()).add(e);
		 }
	 }

	/**
	 * @return the violation
	 */
	public HashMap<String, ArrayList<AccessRuleViolation>> getViolation() {
		return violation;
	}

	/**
	 * @param violation the violation to set
	 */
	public void setViolation(HashMap<String, ArrayList<AccessRuleViolation>> violation) {
		this.violation = violation;
	}

	@Override
	public void mackerAborted(RuleSet arg0) {
        // nothing
	}

	@Override
	public void mackerFinished(RuleSet arg0) throws MackerIsMadException,
			ListenerException {
        // nothing
	}

	@Override
	public void mackerStarted(RuleSet arg0) throws ListenerException {
        //nothing
	}




}
