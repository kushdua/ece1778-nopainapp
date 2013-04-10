package ca.sickkids.nopainapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DiseaseInfoActivity extends Activity{
	
	TextView tv;
	String cancer = "What is Cancer? \n" +
					"Cancer is not just one disease. There are over 100 different types of cancer, each with" +
					"different names, effects, and treatments. Even though each type of cancer is different" +
					"there are some basic things that are similar to most cancers.\n" +
					"In order to understand more about cancer, you first need to understand a bit about" +
					"cells.\n\n" +
					"What are cells? \n" +
					"Cells are the building blocks of the body. They are very small and can only be seen under"+
					"a microscope. There are billions of cells in a fully grown human body. Not every cell"+
					"is the same. There are hundreds of different kinds of cells in the body and each has a"+
					"different job. For example, bone cells are different from skin cells, which are different"+
					"from muscle cells. The cells work together to form organs like the heart, the brain, or"+
					"intestines.\n\n" +
					"Even though cells do different jobs in our body, they still have some similarities. Each" +
					"cell has something called a nucleus that contains DNA. The DNA contains genes that" +
					"are like instructions for our cells. DNA tells cells what to do and how and when to do" +
					"it. Genes within the DNA tell the cells when to reproduce (make copies of themselves)" +
					"and when to die. If a cell is damaged, instructions in the DNA tell the cell either how to" +
					"repair itself, or if it is too damaged, that it is time for the cell to die.\n\n" +
					"How do cells reproduce?\n" +
					"Cells reproduce through a process called cell division. In cell division, the cell copies" +
					"all it’s DNA and then divides into two identical cells. Normally, cells only reproduce to" +
					"replace other cells that have died or to make more cells when your body is growing. This" +
					"way, your body carefully regulates how many cells there are in your body.\n\n" +
					"Different types of cells reproduce at different rates. For example, cells in your mouth" +
					"and that line your stomach and intestines live a short time and reproduce very often." +
					"Cells in your brain and spinal cord, called neurons, live a long time and in adults they" +
					"almost never reproduce.\n\n" +
					"Cells and cancer\n" +
					"Cancer happens when there is a change in the DNA (genes) in a normal cell called" +
					"a mutation. The mutation causes the cell to act differently than a normal cell. The" +
					"mutated cells do not die when they should. They reproduce even when the body does" +
					"not need them to, usually much faster than normal cells. The cancerous cell reproduces" +
					"over and over again, forming two cells, then four, then eight, then 16, until it has made" +
					"billions of copies of itself. Each of the copies has the same mutation./n/n" +
					"How cancer spreads\n" +
					"The place the cancer first started in the body is known as the primary cancer site. We" +
					"call this group of cancer cells the primary cancer. These cancer cells can break away and" +
					"travel to other parts of the body through the blood or lymph vessels. When cancer cells" +
					"start to travel in the body, it is called metastasis. Sometimes these cells can get stuck in" +
					"other tissues in the body and will then start to grow in this new place in the body. This" +
					"new group of cells is called the secondary cancer site.\n\n" +
					"What causes cancer?\n" +
					"Scientists are searching for the answer to what causes cancer. For most cancers though," +
					"especially for cancers in young people, we still do not know what causes them. We" +
					"do know that cancer IS NOT contagious. You cannot catch cancer and cancer does not" +
					"spread from one person to the next.\n\n" +
					"Remember, having cancer yourself does not mean that your brothers, sisters, friends," +
					"or parents will get cancer. Very, very rarely, cancer can be caused by a mutation in the" +
					"genes of your DNA that was passed on through your family. Having this mutated gene" +
					"does not mean that a person will definitely get cancer, but it can increase the risk of" +
					"developing a cancer.\n";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.education_mod);
		//addListenerOnButton();
		 tv = (TextView) findViewById(R.id.textView1);

         tv.setText(cancer);

	}
}
