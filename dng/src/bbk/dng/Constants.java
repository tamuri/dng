package bbk.dng;

import java.awt.Color;

public class Constants {

  public static final String APPLICATION_NAME = "ArchSchema";
  public static final String VERSION = "1.1";
  public static final int MAX_ARCHITECTURES = 150;
  public static final String HEAD_HTML
          = "<head><style type=\"text/css\">\nbody { font-size: 12pt; font-family: sans-serif }\n</style><head>";
  /* RAL 7 Jul 09 --> */
  public static final int MAX_SEQSTR_NODES = 50;
  public static final int VERY_MANY_SEQUENCES = 5000;
  public static final int MAX_SEQS_IN_LIST = 200;
  public static final int[] COVERAGE_COL = {255, 0, 0};
  public static final int[] PARENT_COL = {191, 191, 191};
  public static final int[] SHADOW_COL = {54, 54, 54};
  public static final int NPFAM_COLOURS = 45;
  public static final int PFAMA_STARTCOL = 0;
  public static final int PFAMB_STARTCOL = 10;

  public static final String URL_ARCHSCHEMA_HELP
          = "http://www.ebi.ac.uk/thornton-srv/databases/archschema/documentation.html";
  public static final String URL_ARCHSEARCH
          = "http://www.ebi.ac.uk/thornton-srv/databases/cgi-bin/archschema/ArchSearch.pl?";
  public static final String URL_ARCHINDEX
          = "http://www.ebi.ac.uk/thornton-srv/databases/cgi-bin/archschema/RunArchSchema.jnlp?";
  public static final String URL_EC_PDB
          = "http://www.ebi.ac.uk/thornton-srv/databases/cgi-bin/enzymes/GetPage.pl?ec_number=";
  public static final String URL_PDBSUM
          = "http://www.ebi.ac.uk/pdbsum/";
  public static final String URL_PDBSUM_UNIPLOT
          = "http://www.ebi.ac.uk/thornton-srv/databases/cgi-bin/pdbsum/GetUnichains.pl?uniprot_id=";
  public static final String URL_CATH
          = "http://www.cathdb.info/cathnode/";
  public static final String URL_PFAM
          = "http://pfam.sanger.ac.uk/family?acc=";
  public static final String URL_UNIPROT
          = "http://www.uniprot.org/uniprot/";

  // Node types
  public static final int SEQ_NODE = 0;
  public static final int STRUC_NODE = 1;
  public static final int ENZYME_NODE = 2;

  public final static int    PARENT = 0;
  public final static int    NORMAL = 1;

  // Sizes of boxes depend on context
  // Height
  // ------
  // 1. Pfam domains only plot: PARENT and NORMAL
  // 2. CATH and Pfam domains plot: CATH domains = PARENT and NORMAL
  //                                Pfam domains = PARENT2 and NORMAL2
  // Width
  // -----
  // 1. Pfam domains only plot: PARENT and NORMAL
  // 2. CATH and Pfam domains plot: Ordinary domain = PARENT and NORMAL
  //                                Split domains = PARENT2 and NORMAL2
  public final static double[] recWidth = {9, 6};
  public final static double[] splitCATHWidth = {3, 3};
  public final static double[] pfamAHeight = {18, 12, 14, 9};
  public final static double[] pfamBHeight = {9, 6};
  public final static double[] gap = {5, 3};
  public final static double[] extraForCoverage = {4, 4};
  public final static double[] xMargin = {3, 2};
  public final static double[] yMargin = {3, 2};
  public final static float    EDGE_THICKNESS = 1;
  public final static float    LINE_THICKNESS = (float) 1.5;
  public final static float    TEXT_SIZE = (float) 10.0;

  // Colour types
  public static final int DEFAULT_COLOUR = 0;
  public static final int MIN_COLOUR = 1;
  public static final int MAX_COLOUR = 2;

  public static final int SEQ_MIN_RANGE = 50;
  public static final int SEQ_MAX_RANGE = 1000;

  public static final int[][][] NODE_COLOUR = {
    {{196, 246, 255}, {255, 194, 186}, {255,   0,   0}}, // Sequence node colours
    {{255, 255, 179}, {255, 194, 186}, {255,   0,   0}}, // PDB node colours
    {{  0,   0,   0}, {221, 160, 221}, {160,  32, 240}}, // EC.1 node colours
    {{  0,   0,   0}, {255, 160, 170}, {255,   0,   0}}, // EC.2 node colours
    {{  0,   0,   0}, {176, 196, 222}, { 70, 130, 180}}, // EC.3 node colours
    {{  0,   0,   0}, {192, 255,  62}, {  0, 255,   0}}, // EC.4 node colours
    {{  0,   0,   0}, {244, 232,  87}, {255, 255,   0}}, // EC.5 node colours
    {{  0,   0,   0}, {238, 221, 130}, {139,  69,  19}}, // EC.6 node colours
  };

  public static final int[][] NSEQS_RANGE = {
    {50, 500}, // nSeqs range
    {50, 500}, // nPDB range
    {5, 10}, // nEC range
  };

  public static final String[][] COLOUR_DEFN = {
    {"Green", "#00FF00", "0 255 0"}, //  0
    {"Red", "#FF0000", "255 0 0"}, //  1
    {"Blue", "#0000FF", "0 0 255"}, //  2
    {"Gold", "#FFD700", "255 215 0"}, //  3
    {"Purple", "#A020F0", "160 32 240"}, //  4
    {"Cyan", "#00FFFF", "0 255 255"}, //  5
    {"Firebrick", "#B22222", "178 34 34"}, //  6
    {"LimeGreen", "#32CD32", "50 205 50"}, //  7
    {"DeepSkyBlue", "#00BFFF", "0 191 255"}, //  8
    {"LightGoldenrod1", "#FFEC8B", "255 236 139"}, //  9
    {"OrangeRed", "#FF4500", "255 69 0"}, // 10
    {"LightGreen", "#90EE90", "144 238 144"}, // 11
    {"SkyBlue", "#87CEEB", "135 206 235"}, // 12
    {"HotPink", "#FF69B4", "255 105 180"}, // 13
    {"SeaGreen", "#2E8B57", "46 139 87"}, // 14
    {"MidnightBlue", "#191970", "25 25 112"}, // 15
    {"DarkRed", "#8B0000", "139 0 0"}, // 16
    {"Peru", "#CD853F", "205 133 63"}, // 17
    {"SpringGreen", "#00FF7F", "0 255 127"}, // 18
    {"Magenta", "#FF00FF", "255 0 255"}, // 19
    {"DimGrey", "#696969", "105 105 105"}, // 20
    {"DarkGoldenrod", "#B8860B", "184 134 11"}, // 21
    {"Maroon", "#B03060", "176 48 96"}, // 22
    {"Orange", "#FFA500", "255 165 0"}, // 23
    {"OliveDrab", "#6B8E23", "107 142 35"}, // 24
    {"CornflowerBlue", "#6495ED", "100 149 237"}, // 25
    {"DeepPink", "#FF1493", "255 20 147"}, // 26
    {"DarkSlateBlue", "#483D8B", "72 61 139"}, // 27
    {"SandyBrown", "#F4A460", "244 164 96"}, // 28
    {"DarkKhaki", "#BDB76B", "189 183 107"}, // 29
    {"Tomato", "#FF6347", "255 99 71"}, // 30
    {"SteelBlue", "#4682B4", "70 130 180"}, // 31
    {"Chocolate", "#D2691E", "210 105 30"}, // 32
    {"PaleGreen", "#98FB98", "152 251 152"}, // 33
    {"MediumOrchid", "#BA55D3", "186 85 211"}, // 34
    {"DarkSlateGray", "#2F4F4F", "47 79 79"}, // 35
    {"Salmon", "#FA8072", "250 128 114"}, // 36
    {"SaddleBrown", "#8B4513", "139 69 19"}, // 37
    {"SlateGrey", "#708090", "112 128 144"}, // 38
    {"LawnGreen", "#7CFC00", "124 252 0"}, // 39
    {"Burlywood", "#DEB887", "222 184 135"}, // 40
    {"DarkGreen", "#006400", "0 100 0"}, // 41
    {"SlateBlue", "#6A5ACD", "106 90 205"}, // 42
    {"DarkOliveGreen", "#556B2F", "85 107 47"}, // 43
    {"Grey", "#BEBEBE", "190 190 190"} // 44
  };
  public static final int MIN_COLOUR_DIFF = 20;

  //
  // --- Get the colour equivalent of the given RGB value
  //
  public static final Color getColourFromIntRGB(int red,int green,int blue)
    {
      float hsb[] = new float [3];
      Color colour = Color.black;

      // Convert the RGB number to HSB
      float hvals[] = Color.RGBtoHSB(red,green,blue,hsb);
      colour = Color.getHSBColor(hsb[0],hsb[1],hsb[2]);
      return colour;
    }
}
