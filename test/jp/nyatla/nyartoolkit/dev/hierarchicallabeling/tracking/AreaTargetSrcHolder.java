package jp.nyatla.nyartoolkit.dev.hierarchicallabeling.tracking;

import java.lang.reflect.Array;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.labeling.rlelabeling.NyARRleLabelFragmentInfo;
import jp.nyatla.nyartoolkit.core.types.NyARIntPoint2d;
import jp.nyatla.nyartoolkit.core.types.NyARIntRect;
import jp.nyatla.nyartoolkit.core.types.stack.NyARObjectStack;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.HierarchyRect;



public class AreaTargetSrcHolder extends NyARObjectPool<AreaTargetSrcHolder.AreaSrcItem>
{
	public static class AreaSrcItem
	{
		public NyARIntRect    area  =new NyARIntRect();
		public NyARIntPoint2d area_center=new NyARIntPoint2d();
		/**
		 * エリア矩形の対角距離の2乗値
		 */
		public int area_sq_diagonal;
	}
	protected AreaTargetSrcHolder.AreaSrcItem createElement() throws NyARException
	{
		throw new NyARException();
	}
	public AreaTargetSrcHolder(int i_length) throws NyARException
	{
		super.initInstance(i_length, AreaTargetSrcHolder.AreaSrcItem.class);
	}
	public AreaTargetSrcHolder.AreaSrcItem newSrcTarget(HierarchyRect i_imgmap,NyARRleLabelFragmentInfo info)
	{
		AreaTargetSrcHolder.AreaSrcItem item=this.newObject();
		if(item==null){
			return null;
		}
		int skip=i_imgmap.dot_skip;
		item.area.x=info.clip_l*skip+i_imgmap.x;
		item.area.y=info.clip_t*skip+i_imgmap.y;
		item.area.w=(info.clip_r-info.clip_l)*skip;
		item.area.h=(info.clip_b-info.clip_t)*skip;
		item.area_center.x=item.area.x+item.area.w/2;
		item.area_center.y=item.area.y+item.area.h/2;
		item.area_sq_diagonal=item.area.w*item.area.w+item.area.h*item.area.h;
		return item;
	}	
}

//
///**
// * 範囲情報のみを保管します。
// * このクラスの要素は、他の要素から参照する可能性があります。
// */
//public class AreaTargetSrcHolder extends NyARObjectStack<AreaTargetSrcHolder.AreaSrcItem>
//{
//	public static class AreaSrcItem
//	{
//		public NyARIntRect    area  =new NyARIntRect();
//		public NyARIntPoint2d area_center=new NyARIntPoint2d();
//		/**
//		 * エリア矩形の対角距離の2乗値
//		 */
//		public int area_sq_diagonal;
//	}
//	
//	public AreaTargetSrcHolder.AreaSrcItem pushSrcTarget(HierarchyRect i_imgmap,NyARRleLabelFragmentInfo info)
//	{
//		AreaTargetSrcHolder.AreaSrcItem item=this.prePush();
//		if(item==null){
//			return null;
//		}
//		int skip=i_imgmap.dot_skip;
//		item.area.x=info.clip_l*skip+i_imgmap.x;
//		item.area.y=info.clip_t*skip+i_imgmap.y;
//		item.area.w=(info.clip_r-info.clip_l)*skip;
//		item.area.h=(info.clip_b-info.clip_t)*skip;
//		item.area_center.x=item.area.x+item.area.w/2;
//		item.area_center.y=item.area.y+item.area.h/2;
//		item.area_sq_diagonal=item.area.w*item.area.w+item.area.h*item.area.h;
//		return item;
//	}
//	protected AreaTargetSrcHolder.AreaSrcItem createElement()
//	{
//		return new AreaTargetSrcHolder.AreaSrcItem();
//	}
//	public AreaTargetSrcHolder(int i_size) throws NyARException
//	{
//		super.initInstance(i_size,AreaTargetSrcHolder.AreaSrcItem.class);
//	}
//	
//}