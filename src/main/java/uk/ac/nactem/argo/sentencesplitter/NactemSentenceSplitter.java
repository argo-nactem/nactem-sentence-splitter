/**
 * Sentence Splitter - Sentence splitter with output compatible with Scott Piao's version
 * Copyright © 2017 The National Centre for Text Mining (NaCTeM), University of
							Manchester (jacob.carter@manchester.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.nactem.argo.sentencesplitter;

import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.u_compare.shared.document.text.Paragraph;
import org.u_compare.shared.syntactic.Sentence;

import uk.ac.nactem.tools.sentencesplitter.EnglishSentenceSplitter;

public class NactemSentenceSplitter extends JCasAnnotator_ImplBase {

	private EnglishSentenceSplitter splitter = new EnglishSentenceSplitter();

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			String documentText = cas.getDocumentText();
			AnnotationIndex<Annotation> textSections = cas.getAnnotationIndex(Paragraph.type);

			// Use known paragraphs if available
			if (textSections.iterator().hasNext()) {
				System.err.println("Using paragraphs");
				for (Annotation text : textSections) {
					split(cas, text.getCoveredText(), text.getBegin());
				}
			} else {
				split(cas, documentText, 0);
			}

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void split(JCas cas, String text, int startOffset) throws Exception {
		List<int[]> results = splitter.markupRawText(text);
		for (int[] result : results) {
			int startChar = result[2] + startOffset;
			int endChar = result[3] + startOffset;
			Sentence sentence = new Sentence(cas);
			sentence.setBegin(startChar);
			sentence.setEnd(endChar);
			sentence.addToIndexes();
		}
	}

}
