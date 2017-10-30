package vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.plots.PiePlot;
import de.erichseifert.gral.plots.colors.LinearGradient;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.JFrame;

public class VisionUtil {
	public static void pieChart(String tableName, String fieldName, Map<String, Integer> freqMapSelected) {
		Frame frame = new VisionUtil.Frame(tableName, fieldName, freqMapSelected);
		frame.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	private static class Frame extends JFrame {
		public Frame(String tableName, String fieldName, Map<String, Integer> freqMapSelected) {
			// set frame
			setSize(1500, 700);
	        
			// feed data
	        @SuppressWarnings("unchecked")
			DataTable data = new DataTable(Integer.class, Label.class);
	        for (Map.Entry<String, Integer> entry : freqMapSelected.entrySet()) {
	        	data.add(entry.getValue(), new Label(entry.getKey()));
	        }
			
	        // create plot
			PiePlot plot = new PiePlot(data);
			getContentPane().add(new InteractivePanel(plot));
			((ValueLegend)plot.getLegend()).setLabelColumn(1);
			plot.getTitle().setText(String.format("Table %s Field %s", tableName, fieldName));
			plot.setRadius(0.9);
			plot.setLegendVisible(true);
			plot.setInsets(new Insets2D.Double(20.0, 20.0, 20.0, 20.0));
			
			PiePlot.PieSliceRenderer renderer = (PiePlot.PieSliceRenderer)plot.getPointRenderer(data);
			renderer.setInnerRadius(0.4);
			renderer.setGap(0.2);
			
			LinearGradient colors = new LinearGradient(Color.GREEN, Color.ORANGE);
			renderer.setColor(colors);
			renderer.setValueVisible(true);
			renderer.setValueColor(Color.BLACK);
			renderer.setValueFont(new Font("TimesRoman", Font.BOLD, 20));
			
			add(new InteractivePanel(plot), BorderLayout.CENTER);
		}
	}
	
	private static class Label implements Comparable<Label> {
		private String label;
		
		public Label(String label) {
			this.label = label;
		}
		
		@Override
		public int compareTo(Label that) {
			return this.label.compareTo(that.label);
		}
		
		public String toString() {
			return label;
		}
	}
}

































