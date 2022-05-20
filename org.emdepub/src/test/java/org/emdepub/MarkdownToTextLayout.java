package org.emdepub;

import org.emdepub.activator.F;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import swt_text_layout.help.HelpBand;

public class MarkdownToTextLayout {

	static final Parser PARSER = Parser.builder().build();

    public static void main(String[] args) {
    	String model = """
			#Heading
			-----
			paragraph *bold* text 
			lazy continuation
			
			* list item
			    > block quote
			    lazy continuation
			
			~~~info
			        with uneven indent
			           with uneven indent
			     indented code
			~~~ 
			
			        with uneven indent
			           with uneven indent
			     indented code
			1. numbered item 1   
			1. numbered item 2   
			1. numbered item 3   
			    - bullet item 1   
			    - bullet item 2   
			    - bullet item 3   
			        1. numbered sub-item 1   
			        1. numbered sub-item 2   
			        1. numbered sub-item 3   
			    
			    ~~~info
			            with uneven indent
			               with uneven indent
			         indented code
			    ~~~ 
			    
			            with uneven indent
			               with uneven indent
			         indented code
  			""";

    	String model2 = """
	    	Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse consequat malesuada eros eu gravida. Cras consequat, purus at pretium aliquam, eros sem rutrum purus, vulputate fringilla nisl metus eu urna. Vivamus scelerisque ipsum neque. Aliquam varius posuere ipsum, sit amet eleifend magna pulvinar nec. Sed volutpat risus id tellus tincidunt quis tincidunt leo cursus. Nam molestie ante ac leo rutrum in malesuada felis eleifend. Phasellus volutpat massa at felis interdum sit amet tristique risus viverra.
	    	Sed dignissim tincidunt varius. Donec vulputate nibh sit amet nisl aliquam non sollicitudin metus elementum. Ut convallis scelerisque quam quis gravida. Aenean et pulvinar ante. Aliquam erat volutpat. Maecenas at lorem nulla, et ornare nibh. Nunc ut ante eu purus adipiscing feugiat sodales eu leo. In pharetra ornare velit, nec venenatis eros tristique ac. Fusce venenatis augue sit amet ante malesuada blandit. Sed elit justo, ultrices nec pulvinar in, mollis eget magna. In hac habitasse platea dictumst. Aenean a dignissim ante. Phasellus eget tempus erat. Nullam ante purus, auctor et imperdiet ac, tristique vel leo. Vivamus dapibus cursus mauris non venenatis.
	    	Mauris nec nibh nulla, sed suscipit leo. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce suscipit dolor ut dui faucibus sed sodales enim iaculis. Mauris volutpat libero eget odio tristique non tincidunt magna pretium. Ut vitae erat ut dui sodales luctus. Etiam adipiscing diam et felis rutrum ut pellentesque enim porttitor. Nullam aliquam sapien non magna elementum feugiat. Praesent dapibus gravida erat, non dignissim diam sollicitudin ut.
	    	Suspendisse sit amet elit neque. Cras rutrum luctus mauris, id tincidunt erat gravida consectetur. Mauris id arcu a lacus congue condimentum. Aenean mollis enim vel dolor dignissim condimentum. Duis vestibulum orci nec ante semper blandit. Vestibulum eleifend, tellus nec rutrum laoreet, augue sapien ultrices lacus, vitae dapibus arcu risus in eros. Donec mauris elit, euismod eget commodo in, tristique a sem. Cras posuere vestibulum nisi non ultrices. Curabitur libero ipsum, dapibus vitae feugiat quis, aliquet nec nulla. Etiam leo tortor, scelerisque quis ultricies id, cursus a elit. Morbi in ante ante. Fusce ac massa metus. Nulla vestibulum luctus erat vitae feugiat. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec eros velit, tincidunt in gravida sed, interdum et nulla.
	    	Quisque id mauris libero, in tincidunt eros. Nam non odio at odio congue vehicula. Mauris condimentum, nisi nec faucibus auctor, nisi erat fermentum nunc, eu viverra massa leo non neque. Phasellus vitae velit sapien, faucibus rutrum sem. Curabitur ac risus sit amet sapien sollicitudin accumsan sed sed dui. Nam feugiat condimentum porttitor. Nam at elementum est. Duis dignissim, ligula at fermentum venenatis, leo nulla lacinia orci, non pulvinar diam dui sed felis. In felis mauris, suscipit at sagittis sit amet, aliquet vel diam.
	    	Cras malesuada rhoncus aliquet. Etiam pharetra pulvinar accumsan. Nam a elit quis justo eleifend blandit eget non augue. Praesent non leo nulla, quis ullamcorper metus. Curabitur posuere, magna vel pulvinar faucibus, diam mi facilisis nulla, eu auctor nunc nisl ac justo. Praesent quis ante nunc. In consequat elementum consequat. Sed et lacus tortor. Vivamus quis felis nisi, a rutrum libero.
	    	Praesent ac facilisis enim. Duis mauris nisi, congue ac scelerisque a, pharetra ut libero. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam elementum nulla eget lacus dictum ullamcorper. Nam sit amet tellus in lorem interdum lobortis in eget velit. Integer eget lacus in nulla volutpat fringilla non vitae lectus. Aliquam mollis sem gravida quam consequat sit amet rutrum quam bibendum.
	    	Sed porttitor imperdiet leo et volutpat. Morbi leo risus, sollicitudin id scelerisque et, fermentum ut lectus. Nulla vel leo erat, eu tempus turpis. Fusce interdum aliquet mi, non consequat nunc adipiscing vel. Ut malesuada dolor in tellus ornare at lobortis eros sagittis. Donec dapibus lacus eu tortor porta non ullamcorper lorem convallis. Integer ultricies risus et velit pretium sed porttitor erat tempus. Integer dolor augue, pulvinar ac bibendum id, mollis eget sem. Phasellus et arcu dui. Donec et lectus orci, eu dictum nibh.
	    	Ut lectus erat, lacinia a feugiat quis, congue sed erat. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vulputate, diam vitae accumsan aliquam, orci diam pretium ipsum, sed posuere orci tellus ut est. Aenean et lacus et tellus tempus ullamcorper. Duis vehicula, lectus non luctus suscipit, eros magna ornare ante, id tincidunt ante velit eu elit. Morbi posuere tellus ac nibh dictum lacinia. Nunc porttitor lacinia aliquet. Suspendisse consectetur vehicula leo eu porta. Sed et felis erat, eu mattis ante. In ut orci risus. Etiam pulvinar, nunc id adipiscing commodo, lacus nisi malesuada quam, sed venenatis augue purus a ligula. Etiam volutpat, ligula sit amet mollis vehicula, risus diam tempus neque, id placerat libero tortor in tellus.
	    	Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Maecenas interdum sagittis erat vel tincidunt. Maecenas fringilla fringilla orci. Vivamus tristique tristique velit, eget placerat dui pulvinar a. Nullam id felis risus, aliquam tincidunt sapien. Vivamus dictum vulputate dui quis facilisis. Pellentesque metus nibh, congue a commodo sed, ullamcorper tempor augue. Pellentesque rhoncus dapibus cursus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc consectetur, tortor et congue aliquam, odio purus consequat ligula, vitae varius massa mi et dolor. Maecenas consequat, lorem aliquam sollicitudin suscipit, est libero ultricies urna, volutpat varius nibh odio a mi. Vestibulum eget nibh est, in imperdiet leo. In velit ante, imperdiet id elementum et, consequat id magna. Aenean mollis enim sit amet erat egestas quis pulvinar mi hendrerit. Suspendisse venenatis sollicitudin elementum. 
   			""";

    	String classStart = """
package org.emdepub;

import swt_text_layout.help.HelpPage;
import swt_text_layout.help.HelpBand;

import org.eclipse.swt.graphics.GC;

public class HelpPage%s extends HelpPage {

	public HelpPage%s(int marginLeft, int marginTop, int width, GC gc) {
		super(marginLeft, marginTop, width, gc);
	}

	@Override
	protected void createBands() {
		HelpBand helpBand1 = new HelpBand(this);
		helpBand1.getTextLayout().setText(\"\"\"
%s
\"\"\");
		helpBand1.getTextLayout().setStyle(normalTextStyle, 0, %s);
		this.getBands().add(helpBand1);
	}
}
""";

        //System.out.println(model);
        
        Node document = PARSER.parse(model2);
        TextLayoutCollectingVisitor textLayoutCollectingVisitor = new TextLayoutCollectingVisitor();
        String text = textLayoutCollectingVisitor.collectAndGetText(document);

//        System.out.println("\n\nText\n");
        
        
        System.out.println("-------------------------------------------------------------------------------------------------");
        
        String classOut = classStart.formatted("Sub", "Sub", text, "" + text.length());
        
        System.out.println(classOut);
        
        F.saveStringToFile(classOut, "C:\\Iustin\\Programming\\_emdepub\\repositories\\emdepub\\org.emdepub\\src\\test\\java\\org\\emdepub\\HelpPageSub.java");
        
    }
}
