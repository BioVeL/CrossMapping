/*
 * #%L
 * Xmap Utils
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owner, 
 * Cardiff University.
 * #L%
 */
package org.openbio.xmap.common.utils.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;

/**
 * Class that implements XMLStreamWriter adding to it the possibility of indentation
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @see javax.xml.stream.XMLStreamWriter
 */
public class XMLPrettyPrinter implements XMLStreamWriter {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
    /**
     * XMLStreamWriter used
     */
	private XMLStreamWriter out;
	
	/**
	 * string with the text to introduce in the indentation
	 */
    private String indent;
    
    /**
     * current level of indentation
     */
    private int indentLevel = 0;
    
    /**
     * boolean that indicate the suppress
     */
    private boolean suppress = false;

    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    public XMLPrettyPrinter(XMLStreamWriter out, String indent) {
        this.out = out;
        this.indent = indent;
    }

    @Override
    public void writeStartElement(String s) throws XMLStreamException {
        addIndent();
        ++indentLevel;
        out.writeStartElement(s);
    }

    @Override
    public void writeStartElement(String s, String s1) throws XMLStreamException {
        addIndent();
        ++indentLevel;
        out.writeStartElement(s, s1);
    }

    @Override
    public void writeStartElement(String s, String s1, String s2) throws XMLStreamException {
        addIndent();
        ++indentLevel;
        out.writeStartElement(s, s1, s2);
    }

    @Override
    public void writeEmptyElement(String s, String s1) throws XMLStreamException {
        addIndent();
        out.writeEmptyElement(s, s1);
    }

    @Override
    public void writeEmptyElement(String s, String s1, String s2) throws XMLStreamException {
        addIndent();
        out.writeEmptyElement(s, s1, s2);
    }

    @Override
    public void writeEmptyElement(String s) throws XMLStreamException {
        addIndent();
        out.writeEmptyElement(s);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        --indentLevel;
        addIndent();
        out.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        out.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException {
        out.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        out.flush();
    }

    @Override
    public void writeAttribute(String s, String s1) throws XMLStreamException {
        out.writeAttribute(s, s1);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2, String s3) throws XMLStreamException {
        out.writeAttribute(s, s1, s2, s3);
    }

    @Override
    public void writeAttribute(String s, String s1, String s2) throws XMLStreamException {
        out.writeAttribute(s, s1, s2);
    }

    @Override
    public void writeNamespace(String s, String s1) throws XMLStreamException {
        out.writeNamespace(s, s1);
    }

    @Override
    public void writeDefaultNamespace(String s) throws XMLStreamException {
        out.writeDefaultNamespace(s);
    }

    @Override
    public void writeComment(String s) throws XMLStreamException {
        out.writeComment(s);
    }

    @Override
    public void writeProcessingInstruction(String s) throws XMLStreamException {
        out.writeProcessingInstruction(s);
    }

    @Override
    public void writeProcessingInstruction(String s, String s1) throws XMLStreamException {
        out.writeProcessingInstruction(s, s1);
    }

    @Override
    public void writeCData(String s) throws XMLStreamException {
        out.writeCData(s);
    }

    @Override
    public void writeDTD(String s) throws XMLStreamException {
        out.writeDTD(s);
    }

    @Override
    public void writeEntityRef(String s) throws XMLStreamException {
        out.writeEntityRef(s);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        out.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String s) throws XMLStreamException {
        out.writeStartDocument(s);
    }

    @Override
    public void writeStartDocument(String s, String s1) throws XMLStreamException {
        out.writeStartDocument(s, s1);
    }

    @Override
    public void writeCharacters(String s) throws XMLStreamException {
        out.writeCharacters(s);
        suppress = true;
    }

    @Override
    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        out.writeCharacters(chars, i, i1);
        suppress = true;
    }

    @Override
    public String getPrefix(String s) throws XMLStreamException {
        return out.getPrefix(s);
    }

    @Override
    public void setPrefix(String s, String s1) throws XMLStreamException {
        out.setPrefix(s, s1);
    }

    @Override
    public void setDefaultNamespace(String s) throws XMLStreamException {
        out.setDefaultNamespace(s);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        out.setNamespaceContext(namespaceContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return out.getNamespaceContext();
    }

    @Override
    public Object getProperty(String s) throws IllegalArgumentException {
        return out.getProperty(s);
    }
    
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to add indentation to the xml stream 
     * @throws XMLStreamException
     */
    private void addIndent() throws XMLStreamException {
        if (suppress) {
            suppress = false;
        }
        else {
            out.writeCharacters('\n' + StringUtils.repeat(indent, indentLevel));
        }
    }    
}
