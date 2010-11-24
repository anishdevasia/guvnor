/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.ruleeditor;

import java.util.List;

import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a popup list for "content assistance" - although on the web, 
 * its not assistance - its mandatory ;)
 */
public class ChoiceList extends PopupPanel {

    private ListBox             list;
    private final DSLSentence[] sentences;
    private HorizontalPanel     buttons;
    private TextBox             filter;
    private Constants           constants = ((Constants) GWT.create( Constants.class ));

    /**
    * Pass in a list of suggestions for the popup lists.
    * Set a click listener to get notified when the OK button is clicked.
    */
    public ChoiceList(final DSLSentence[] sen,
                      final DSLRuleEditor self) {
        super( true );

        setGlassEnabled( true );
        this.sentences = sen;
        filter = new TextBox();
        filter.setWidth( "100%" );
        final String defaultMessage = constants.enterTextToFilterList();
        filter.setText( defaultMessage );
        filter.addFocusHandler( new FocusHandler() {
            public void onFocus(FocusEvent event) {
                filter.setText( "" );
            }
        } );

        filter.addBlurHandler( new BlurHandler() {
            public void onBlur(BlurEvent event) {
                filter.setText( defaultMessage );
            }
        } );

        filter.addKeyUpHandler( new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    applyChoice( self );
                } else {
                    populateList( ListUtil.filter( sentences,
                                                   filter.getText() ) );
                }
            }

        } );
        filter.setFocus( true );

        VerticalPanel panel = new VerticalPanel();
        panel.add( filter );

        list = new ListBox();
        list.setVisibleItemCount( 5 );

        populateList( ListUtil.filter( this.sentences,
                                       "" ) );

        panel.add( list );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                applyChoice( self );
            }
        } );

        Button cancel = new Button( constants.Cancel() );
        cancel.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        } );

        buttons = new HorizontalPanel();

        buttons.add( ok );
        buttons.add( cancel );

        panel.add( buttons );

        add( panel );
        setStyleName( "ks-popups-Popup" ); //NON-NLS

    }

    private void applyChoice(final DSLRuleEditor self) {
        self.insertText( getSelectedItem() );
        hide();
    }

    private void populateList(List<DSLSentence> filtered) {
        list.clear();
        for ( int i = 0; i < filtered.size(); i++ ) {
            list.addItem( ((DSLSentence) filtered.get( i )).sentence );
        }
    }

    public String getSelectedItem() {
        return list.getItemText( list.getSelectedIndex() );
    }

}