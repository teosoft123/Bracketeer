package com.chookapp.org.bracketeer.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import com.chookapp.org.bracketeer.Activator;
import com.chookapp.org.bracketeer.core.IActiveProcessorListener;
import com.chookapp.org.bracketeer.preferences.PreferencesConstants;

public class BracketeerToggleState extends State implements 
    IPropertyChangeListener, IExecutableExtension, ISourceProviderListener
{
    private String _attrName;
    private String _attrSuffix;
    private IPreferenceStore _store;
    
    public final static String STATE_ID = "org.eclipse.ui.commands.toggleState";
    
    public BracketeerToggleState()
    {
        _attrName = null;
        
        _store = Activator.getDefault().getPreferenceStore();
        _store.addPropertyChangeListener(this);
        
        ISourceProviderService srcService = (ISourceProviderService) PlatformUI.getWorkbench().getService(ISourceProviderService.class);
        ISourceProvider src = srcService.getSourceProvider(SourceProvider.PLUGIN_NAME);
        src.addSourceProviderListener(this);
        
        super.setValue(false);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        if( _attrName == null )
            return;
        
        if( !event.getProperty().equals(_attrName) )
            return;
                
        setValue(_store.getBoolean(_attrName));
    }

    @Override
    public void setValue(Object value)
    {
        if (!(value instanceof Boolean)) 
        {
            throw new IllegalArgumentException(
                    "ToggleState takes a Boolean as a value"); //$NON-NLS-1$
        }
        
        if( _attrName == null )
            throw new IllegalStateException("Attr name was not set");
        
        _store.setValue(_attrName, (Boolean) value);
        
        super.setValue(value);
    }

    @Override
    public void setInitializationData(IConfigurationElement config,
                                      String propertyName, Object data) throws CoreException
    {
        if( data == null || !(data instanceof String))
            throw new IllegalArgumentException("must specify attr suffix");
        
        _attrSuffix = (String) data;
    }  

    @SuppressWarnings("rawtypes")
    @Override
    public void sourceChanged(int sourcePriority, Map sourceValuesByName)
    {
        Set set = sourceValuesByName.entrySet();
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            Map.Entry<String, Object> entry = (Entry<String, Object>) it.next();
            sourceChanged(sourcePriority, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void sourceChanged(int sourcePriority, String sourceName,
                              Object sourceValue)
    {
        if( !sourceName.equals(SourceProvider.PLUGIN_NAME) )
            return;
        
        String processorName = (String) sourceValue;
        if( processorName == null || processorName.isEmpty())
        {
            _attrName = null;
        }
        else
        {
            _attrName = PreferencesConstants.preferencePath(processorName) + _attrSuffix;
            super.setValue(_store.getBoolean(_attrName));
        }
    }

}
