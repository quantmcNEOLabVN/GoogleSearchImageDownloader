import java.awt.*;
import javax.swing.*;
public class FilterGroup extends JPanel{
	public JRadioButton all, icon, small, medium, large, xlarge, xxlarge, huge;
	public ButtonGroup bGroup;
	public FilterGroup() {
		super();
		bGroup=new ButtonGroup();
		all=new JRadioButton("All",true);
		icon=new JRadioButton("icon",false);
		small=new JRadioButton("small",false);
		medium=new JRadioButton("medium",false);
		large=new JRadioButton("large",false);
		xlarge=new JRadioButton("xlarge",false);
		xxlarge=new JRadioButton("xxlarge",false);
		huge=new JRadioButton("huge",false);

		bGroup.add(all); 	bGroup.add(icon);	bGroup.add(small);		bGroup.add(medium);
		bGroup.add(large); bGroup.add(xlarge);	bGroup.add(xxlarge);	bGroup.add(huge);

		setLayout(new GridLayout(2, 4));
		
		add(all); 	add(icon);		add(small);		add(medium);
		add(large); add(xlarge);	add(xxlarge);	add(huge);
	}
	
	@Override
	public void setVisible(boolean b) {
		JRadioButton[] lrB= {all, icon, small, medium, large, xlarge, xxlarge, huge};
		for (JRadioButton jrb:lrB)
			jrb.setVisible(b);
		super.setVisible(b);
	}
	
	@Override
	public void setEnabled(boolean b) {
		JRadioButton[] lrB= {all, icon, small, medium, large, xlarge, xxlarge, huge};
		for (JRadioButton jrb:lrB)
			jrb.setEnabled(b);
		super.setEnabled(b);
	}
	public String getSelected() {
		JRadioButton[] lrB= {icon, small, medium, large, xlarge, xxlarge, huge};
		for (JRadioButton jrb:lrB)
			if (jrb.isSelected()==true)
				return jrb.getText();
		return null;
	}
}
