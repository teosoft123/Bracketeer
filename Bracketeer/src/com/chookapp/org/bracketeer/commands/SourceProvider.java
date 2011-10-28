package com.chookapp.org.bracketeer.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.chookapp.org.bracketeer.core.IActiveProcessorListener;

public class SourceProvider extends AbstractSourceProvider implements IActiveProcessorListener
{
    public final static String PLUGIN_NAME = "com.chookapp.org.bracketeer.pluginName";
    
    private String _pluginName; 
    
    public SourceProvider()
    {
        _pluginName = "";
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public Map getCurrentState()
    {
        Map<String, String> currentState = new HashMap<String, String>(1);
        currentState.put(PLUGIN_NAME, _pluginName);
        return currentState;
    }

    @Override
    public String[] getProvidedSourceNames()
    {
        return new String[] {PLUGIN_NAME};
    }
   
    @Override
    public void activeProcessorChanged(String processorName)
    {
        if( processorName == null ) 
            processorName = "";
        
        if( _pluginName.equals(processorName) )
            return;
        
        _pluginName = processorName;
        fireSourceChanged(ISources.WORKBENCH, PLUGIN_NAME, processorName);
    }

}
