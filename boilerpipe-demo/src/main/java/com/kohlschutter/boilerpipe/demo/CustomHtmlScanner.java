/*package com.kohlschutter.boilerpipe.demo;

import java.io.IOException;

import org.cyberneko.html.HTMLScanner;

public class CustomHtmlScanner extends HTMLScanner{
   
	public boolean scan(boolean complete) throws IOException {
        boolean next;
        do {
            try {
                next = false;
                switch (fScannerState) {
                    case STATE_CONTENT: {
                        fBeginLineNumber = fCurrentEntity.getLineNumber();
                        fBeginColumnNumber = fCurrentEntity.getColumnNumber();
                        fBeginCharacterOffset = fCurrentEntity.getCharacterOffset();
                        int c = fCurrentEntity.read();
                        if (c == '<') {
                            setScannerState(STATE_MARKUP_BRACKET);
                            next = true;
                        }
                        else if (c == '&') {
                            scanEntityRef(fStringBuffer, true);
                        }
                        else if (c == -1) {
                            throw new EOFException();
                        }
                        else {
                        	fCurrentEntity.rewind();
                            scanCharacters();
                        }
                        break;
                    }
                    case STATE_MARKUP_BRACKET: {
                        int c = fCurrentEntity.read();
                        if (c == '!') {
                            if (skip("--", false)) {
                                scanComment();
                            }
                            else if (skip("[CDATA[", false)) {
                                scanCDATA();
                            }

}
*/