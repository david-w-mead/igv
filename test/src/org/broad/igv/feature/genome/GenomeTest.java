/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.feature.genome;

import org.broad.igv.util.TestUtils;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Jim Robinson
 * @date 10/31/11
 */
public class GenomeTest {
    @Test
    public void testGetNCBIName() throws Exception {

        String ncbiID = "gi|125745044|ref|NC_002229.3|";
        String ncbiName = "NC_002229.3";

        assertEquals(ncbiName, Genome.getNCBIName(ncbiID));

    }


    @Test
    public void testOrderChromos() throws Exception {
        //Scratch work for now, test methods for separating
        //contigs into "small" and "large"
        String indexPath = TestUtils.DATA_DIR + "fasta/CE.cns.all.fa.fai";
        Sequence seq = new MockSequence(indexPath);
        Genome genome = new Genome("GenomeeTest", "GenomeTest", seq, false);
        List<String> actNames = genome.getAllChromosomeNames();

        String[] expNames = {"chr1", "chr2", "chr3", "chrX", "C121713571", "scaffold22502"};
        int[] expInds = {0, 1, 2, 21, 22, actNames.size() - 1};
        int counter = 0;
        for (int expInd : expInds) {
            String expName = expNames[counter];
            String actName = actNames.get(expInd);
            assertEquals(expName, actName);
            counter++;
        }

    }


    @Test
    public void testGetLongChromosomeNames_manySmall() throws Exception{
        String mockIndexPath = TestUtils.DATA_DIR + "fasta/mock_many_small.fa.fai";
        Sequence sequence = new MockSequence(mockIndexPath);
        Genome genome = new Genome("mock_many_small", "mock_many_small", sequence, true);

        assertNotNull(genome.getLongChromosomeNames());
        assertTrue("No 'Long' chromosome names found", genome.getLongChromosomeNames().size() > 0);
    }

    /**
     * Class which loads FastaIndex and returns information contained therein,
     * but doesn't actually load full fasta file. For testing
     */
    private class MockSequence implements Sequence {

        private FastaIndex index;

        public MockSequence(String fastaIndexPath) throws IOException {
            this.index = new FastaIndex(fastaIndexPath);
        }

        @Override
        public byte[] getSequence(String chr, int start, int end) {
            return new byte[0];
        }

        @Override
        public byte getBase(String chr, int position) {
            return 0;
        }

        @Override
        public List<String> getChromosomeNames() {
            return new ArrayList<String>(index.getSequenceNames());
        }

        @Override
        public int getChromosomeLength(String chrname) {
            return index.getSequenceSize(chrname);
        }
    }

    public static void generateJunkIndex() throws Exception{
        //Generate index file with many small contigs
        int numContigs = 10000;
        int contigMeanSize = 3000;
        int contigSizeRange = 400;
        PrintWriter writer = new PrintWriter(new FileWriter(TestUtils.DATA_DIR + "fasta/mock_many_small.fa.fai"));

        int position = -1;
        int basesPerLine = 80;
        int bytesPerLine = 81;
        for(int ci = 0; ci < numContigs; ci++){
            String chr = "" + ci;
            int size = contigMeanSize + (int) (contigSizeRange * (Math.random() - 0.5));

            String line = String.format("%s\t%d\t%d\t%d\t%d", chr, size, position, basesPerLine, bytesPerLine);
            writer.println(line);
        }
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) throws Exception{
        //generateJunkIndex();
    }
}