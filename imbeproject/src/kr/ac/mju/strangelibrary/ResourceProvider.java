package kr.ac.mju.strangelibrary;

import android.content.res.Resources;

import com.nemustech.tiffany.world.TFContentProvider;
import com.nemustech.tiffany.world.TFModel;
import com.nemustech.tiffany.world.TFObject;
import com.nemustech.tiffany.world.TFTextureInfo;
//created by 60022495 정민규
//created date : 2012/12/07
//last modify : 2012/12/07
//메인액티비티가 명지대에서 구입한 3d프레임웍인 tiffany를 사용했는데, 거기서 필요한 객체. 소스는 그냥 가져와서 정확한 동작은 잘 모르겠습니다.
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

