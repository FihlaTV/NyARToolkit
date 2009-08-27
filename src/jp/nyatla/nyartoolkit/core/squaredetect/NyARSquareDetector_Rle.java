package jp.nyatla.nyartoolkit.core.squaredetect;



import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.labeling.LabelOverlapChecker;
import jp.nyatla.nyartoolkit.core.labeling.rlelabeling.*;
import jp.nyatla.nyartoolkit.core.param.NyARCameraDistortionFactor;
import jp.nyatla.nyartoolkit.core.raster.NyARBinRaster;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;



public class NyARSquareDetector_Rle implements INyARSquareDetector
{
	private static final int AR_AREA_MAX = 100000;// #define AR_AREA_MAX 100000
	private static final int AR_AREA_MIN = 70;// #define AR_AREA_MIN 70
	private final int _width;
	private final int _height;

	private final NyARLabeling_Rle _labeling;

	private final LabelOverlapChecker<RleLabelFragmentInfoStack.RleLabelFragmentInfo> _overlap_checker = new LabelOverlapChecker<RleLabelFragmentInfoStack.RleLabelFragmentInfo>(32,RleLabelFragmentInfoStack.RleLabelFragmentInfo.class);
	private final SquareContourDetector _sqconvertor;
	private final ContourPickup _cpickup=new ContourPickup();
	private final RleLabelFragmentInfoStack _stack;
	
	private final int _max_coord;
	private final int[] _xcoord;
	private final int[] _ycoord;
	/**
	 * 最大i_squre_max個のマーカーを検出するクラスを作成する。
	 * 
	 * @param i_param
	 */
	public NyARSquareDetector_Rle(NyARCameraDistortionFactor i_dist_factor_ref,NyARIntSize i_size) throws NyARException
	{
		this._width = i_size.w;
		this._height = i_size.h;
		//ラベリングのサイズを指定したいときはsetAreaRangeを使ってね。
		this._labeling = new NyARLabeling_Rle(this._width,this._height);
		this._labeling.setAreaRange(AR_AREA_MAX, AR_AREA_MIN);
		this._sqconvertor=new SquareContourDetector(i_size,i_dist_factor_ref);
		this._stack=new RleLabelFragmentInfoStack(i_size.w*i_size.h*2048/(320*240)+32);//検出可能な最大ラベル数
		

		// 輪郭の最大長は画面に映りうる最大の長方形サイズ。
		int number_of_coord = (this._width + this._height) * 2;

		// 輪郭バッファは頂点変換をするので、輪郭バッファの２倍取る。
		this._max_coord = number_of_coord;
		this._xcoord = new int[number_of_coord * 2];
		this._ycoord = new int[number_of_coord * 2];
		return;
	}

	/**
	 * arDetectMarker2を基にした関数
	 * この関数はNyARSquare要素のうち、directionを除くパラメータを取得して返します。
	 * directionの確定は行いません。
	 * @param i_raster
	 * 解析する２値ラスタイメージを指定します。
	 * @param o_square_stack
	 * 抽出した正方形候補を格納するリスト
	 * @throws NyARException
	 */
	public final void detectMarker(NyARBinRaster i_raster, NyARSquareStack o_square_stack) throws NyARException
	{
		final RleLabelFragmentInfoStack flagment=this._stack;
		final LabelOverlapChecker<RleLabelFragmentInfoStack.RleLabelFragmentInfo> overlap = this._overlap_checker;

		// マーカーホルダをリセット
		o_square_stack.clear();

		// ラベル数が0ならここまで
		final int label_num=this._labeling.labeling(i_raster, 0, i_raster.getHeight(), flagment);
		if (label_num < 1) {
			return;
		}
		//ラベルをソートしておく
		flagment.sortByArea();
		//ラベルリストを取得
		RleLabelFragmentInfoStack.RleLabelFragmentInfo[] labels=flagment.getArray();

		final int xsize = this._width;
		final int ysize = this._height;
		final int[] xcoord = this._xcoord;
		final int[] ycoord = this._ycoord;
		final int coord_max = this._max_coord;


		//重なりチェッカの最大数を設定
		overlap.setMaxLabels(label_num);

		for (int i=0; i < label_num; i++) {
			final RleLabelFragmentInfoStack.RleLabelFragmentInfo label_pt=labels[i];
			final int label_area = label_pt.area;
		
			// クリップ領域が画面の枠に接していれば除外
			if (label_pt.clip_l == 0 || label_pt.clip_r == xsize-1){
				continue;
			}
			if (label_pt.clip_t == 0 || label_pt.clip_b == ysize-1){
				continue;
			}
			// 既に検出された矩形との重なりを確認
			if (!overlap.check(label_pt)) {
				// 重なっているようだ。
				continue;
			}
			
			// 輪郭を取得
			final int coord_num = _cpickup.getContour(i_raster,label_pt.entry_x,label_pt.clip_t, coord_max, xcoord, ycoord);
			if (coord_num == coord_max) {
				// 輪郭が大きすぎる。
				continue;
			}
			//輪郭分析用に正規化する。
			final int vertex1 = SquareContourDetector.normalizeCoord(xcoord, ycoord, coord_num);

			//ここから先が輪郭分析
			NyARSquare square_ptr = o_square_stack.prePush();
			if(!this._sqconvertor.coordToSquare(xcoord,ycoord,vertex1,coord_num,label_area,square_ptr)){
				o_square_stack.pop();// 頂点の取得が出来なかったので破棄
				continue;				
			}
			// 検出済の矩形の属したラベルを重なりチェックに追加する。
			overlap.push(label_pt);
		}
		return;
	}
	/**
	 * デバック用API
	 * @return
	 */
	public RleLabelFragmentInfoStack _getFragmentStack()
	{
		return this._stack;
	}

}



