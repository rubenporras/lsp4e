/*******************************************************************************
 * Copyright (c) 2025 Vegard IT GmbH and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Sebastian Thomschke (Vegard IT GmbH) - initial implementation.
 *******************************************************************************/
package org.eclipse.lsp4e.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4j.Range;

/**
 * Utility methods for computing identifier regions around a given offset.
 * <p>
 * In this context an identifier is a contiguous sequence of characters for
 * which {@link Character#isUnicodeIdentifierPart(char)} returns {@code true}.
 * </p>
 */
public final class IdentifierUtil {

	/**
	 * Computes the identifier range (LSP {@link Range}) around the given offset.
	 *
	 * @param document
	 *            the document
	 * @param offset
	 *            the offset inside the document
	 * @return the identifier range
	 * @throws BadLocationException
	 *             if the offset is outside the document
	 */
	public static Range computeIdentifierRange(final IDocument document, int offset) throws BadLocationException {
		final Region region = computeIdentifierRegion(document, offset);
		final int start = region.getOffset();
		final int end = start + region.getLength();
		return new Range(LSPEclipseUtils.toPosition(start, document), LSPEclipseUtils.toPosition(end, document));
	}

	/**
	 * Computes the identifier region (Eclipse {@link Region}) around the given
	 * offset, expanding to include all adjacent identifier characters.
	 *
	 * @param document
	 *            the document
	 * @param offset
	 *            the offset inside the document
	 * @return the identifier region
	 * @throws BadLocationException
	 *             if the offset is outside the document
	 */
	public static Region computeIdentifierRegion(final IDocument document, int offset) throws BadLocationException {
		final int docLength = document.getLength();
		if (offset < 0 || offset > docLength) {
			throw new BadLocationException();
		}
		int start = offset;
		while (start > 0 && isIdentifierPart(document, start - 1)) {
			start--;
		}
		int end = offset;
		while (end < docLength && isIdentifierPart(document, end)) {
			end++;
		}
		return new Region(start, end - start);
	}

	/**
	 * Returns whether the given character is considered part of an identifier.
	 * Delegates to {@link Character#isUnicodeIdentifierPart}.
	 */
	public static boolean isIdentifierPart(final char ch) {
		return Character.isUnicodeIdentifierPart(ch);
	}

	/**
	 * Returns whether the character at the given document offset is considered part
	 * of an identifier.
	 *
	 * @param document
	 *            the document
	 * @param offset
	 *            the character offset
	 * @return {@code true} if the character is an identifier part
	 * @throws BadLocationException
	 *             if the offset is outside the document
	 */
	public static boolean isIdentifierPart(final IDocument document, final int offset) throws BadLocationException {
		return isIdentifierPart(document.getChar(offset));
	}

	private IdentifierUtil() {
	}
}
