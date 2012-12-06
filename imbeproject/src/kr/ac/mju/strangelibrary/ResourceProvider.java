package kr.ac.mju.strangelibrary;

import android.content.res.Resources;

import com.nemustech.tiffany.world.TFContentProvider;
import com.nemustech.tiffany.world.TFModel;
import com.nemustech.tiffany.world.TFObject;
import com.nemustech.tiffany.world.TFTextureInfo;

public class ResourceProvider extends TFContentProvider<Integer> {
	private String TAG = "ResourceProvider";
	private Resources mResources;
	private int mTextureAlignMode = TFTextureInfo.TEXTURE_ALIGN_DEFAULT;
	
	public ResourceProvider(Resources resources) {
		super();
		mResources = resources;
	}
	
	public ResourceProvider(Resources resources, int textureAlignMode) {
		super();
		mResources = resources;
		mTextureAlignMode = textureAlignMode;
	}
	
	public ResourceProvider(Resources resources, int[] list) {
		this(resources);
		setResourceList(list);
	}
	
	public ResourceProvider(Resources resources, int[] list, int textureAlignMode) {
		this(resources, textureAlignMode);
		setResourceList(list);
	}
	
	public void setResourceList(int[] list) {
		for (int i=0; i<list.length; i++) {
			addItem(Integer.valueOf(list[i]));
		}		
	}

	@Override
	protected void applyContent(TFObject object, int itemIndex) {
		((TFModel)object).setImageResource(0, mResources, getItem(itemIndex), mTextureAlignMode);		
	}
}

