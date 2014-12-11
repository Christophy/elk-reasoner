/**
 * 
 */
package org.semanticweb.elk.explanations.tree;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
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
 * #L%
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author pavel
 *
 */
public class ProofNodeRenderer implements TreeCellRenderer {

    private OWLEditorKit owlEditorKit;

    private DefaultTreeCellRenderer treeCellRendererDelegate;

    private PatchedOWLCellRenderer owlCellRenderer;

    public ProofNodeRenderer(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        this.treeCellRendererDelegate = new DefaultTreeCellRenderer();
        this.owlCellRenderer = new PatchedOWLCellRenderer(owlEditorKit);
    }

	@Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    	Component result = null;
    	
    	if(value instanceof OWLExpressionNode) {
    		OWLExpressionNode node = (OWLExpressionNode) value; 
        	OWLAxiom axiom = node.getAxiom();
            
            if (axiom != null) {
            	OWLModelManager manager = owlEditorKit.getModelManager();
                String valueToRender = manager.getRendering(axiom);
                
                //owlCellRenderer.setIconObject(axiom);
                owlCellRenderer.setOntology(owlEditorKit.getOWLModelManager().getActiveOntology());
                owlCellRenderer.setInferred(true);
                owlCellRenderer.setHighlightKeywords(true);
                owlCellRenderer.setOpaque(true);
                owlCellRenderer.useBold(node.isAsserted());
                
                if (tree.getParent() != null) {
                	ProofTreeUI ui = (ProofTreeUI) tree.getUI();
                	
                	owlCellRenderer.setPreferredWidth(ProofTreeUI.getNodePreferredWidth(tree.getParent().getWidth(), ui.getRowX(row, node.getLevel())));
                	
                	/*System.err.println(valueToRender);
                	System.err.println(tree.getParent().getWidth());
                	System.err.println(row + ", " + node.getLevel());
                	System.err.println(ui.getRowX(row, node.getLevel()));
                	System.err.println(owlCellRenderer.getPreferredWidth());*/
                }
                else {
                	owlCellRenderer.setPreferredWidth(-1);
                }
                
                result = owlCellRenderer.getTreeCellRendererComponent(tree, valueToRender, selected, expanded, leaf, row, hasFocus);
                
                if (!node.isAsserted()) {
                	if (!selected) {
                		((JComponent)result).setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 5, 2));
                		result.setBackground(new Color(255, 255, 215));
                	}
                }
                else {
                	((JComponent)result).setBorder(BorderFactory.createEmptyBorder());
                }
                
            }
        }
    	
    	if (result == null) {
    		// separator nodes
    		result = treeCellRendererDelegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    		
    		Dimension size = result.getSize();
    		
    		JPanel separator = new JPanel(new FlowLayout(FlowLayout.LEFT));
    		JLabel ruleText = new JLabel();
    		
    		ruleText.setForeground(Color.GRAY);
    		ruleText.setText(((DefaultMutableTreeNode) value).getUserObject().toString());
    		ruleText.setBackground(tree.getBackground());
    		ruleText.setVisible(true);
    		separator.add(ruleText);
    		separator.setSize(size);
    		separator.setBackground(tree.getBackground());
    		separator.setVisible(true);
    		result = separator;
    	}
    	
    	return result;
    }
	
}
