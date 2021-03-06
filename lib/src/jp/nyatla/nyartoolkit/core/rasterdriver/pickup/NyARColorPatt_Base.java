/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2008-2012 Ryo Iizuka
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as publishe
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp> or <nyatla(at)nyatla.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.rasterdriver.pickup;

import jp.nyatla.nyartoolkit.core.NyARRuntimeException;
import jp.nyatla.nyartoolkit.core.math.NyARMat;
import jp.nyatla.nyartoolkit.core.raster.rgb.*;
import jp.nyatla.nyartoolkit.core.raster.rgb.format.NyARRgbRaster_INT1D_X8R8G8B8_32;
import jp.nyatla.nyartoolkit.core.rasterdriver.perspectivecopy.INyARPerspectiveCopy;
import jp.nyatla.nyartoolkit.core.rasterdriver.perspectivecopy.NyARPerspectiveCopyFactory;
import jp.nyatla.nyartoolkit.core.types.*;

/**
 * このクラスは、画像からパターンを取得する機能を備えたRGBラスタです。
 * ARToolKit由来のアルゴリズムで画像からパターン取得する機能を提供します。
 * この関数は可読性を重視しているため低速です。高速な{@link NyARColorPatt_O3}を使ってください。
 */
public class NyARColorPatt_Base extends NyARRgbRaster_INT1D_X8R8G8B8_32 implements INyARColorPatt
{
	protected static final int AR_PATT_SAMPLE_NUM = 64;
	protected final static double[][] CPARAM_WORLD = {{ 100.0, 100.0 }, { 100.0 + 10.0, 100.0 }, { 100.0 + 10.0, 100.0 + 10.0 },{ 100.0, 100.0 + 10.0 } };
	
	/**
	 * コンストラクタです。
	 * 解像度を指定して、インスタンスを生成します。
	 * @param i_width
	 * ラスタのサイズ
	 * @param i_height
	 * ラスタのサイズ
	 * @throws NyARRuntimeException 
	 */
	protected NyARColorPatt_Base(int i_width, int i_height)
	{
		super(i_width,i_height,true);
		//入力制限
		assert i_width<=64 && i_height<=64;
	}

	/**
	 * この関数は、射影変換パラメータを計算します。
	 * @param i_vertex
	 * 変換元の４角系を定義する頂点配列。４頂点である必要がある。
	 * @param o_para
	 * 計算したパラメータの出力先配列
	 * @return
	 * 計算に成功するとtrueです。
	 * @throws NyARRuntimeException
	 */
	private boolean get_cpara(final NyARIntPoint2d[] i_vertex, NyARMat o_para)
	{
		double[][] world = CPARAM_WORLD;
		NyARMat a = new NyARMat(8, 8);// 次処理で値を設定するので、初期化不要// new NyARMat( 8, 8 );
		double[][] a_array = a.getArray();
		NyARMat b = new NyARMat(8, 1);// 次処理で値を設定するので、初期化不要// new NyARMat( 8, 1 );
		double[][] b_array = b.getArray();
		double[] a_pt0, a_pt1;
		double[] world_pti;

		for (int i = 0; i < 4; i++) {
			a_pt0 = a_array[i * 2];
			a_pt1 = a_array[i * 2 + 1];
			world_pti = world[i];

			a_pt0[0] = (double) world_pti[0];// a->m[i*16+0] = world[i][0];
			a_pt0[1] = (double) world_pti[1];// a->m[i*16+1] = world[i][1];
			a_pt0[2] = 1.0;// a->m[i*16+2] = 1.0;
			a_pt0[3] = 0.0;// a->m[i*16+3] = 0.0;
			a_pt0[4] = 0.0;// a->m[i*16+4] = 0.0;
			a_pt0[5] = 0.0;// a->m[i*16+5] = 0.0;
			a_pt0[6] = (double) (-world_pti[0] * i_vertex[i].x);// a->m[i*16+6]= -world[i][0]*vertex[i][0];
			a_pt0[7] = (double) (-world_pti[1] * i_vertex[i].x);// a->m[i*16+7]=-world[i][1]*vertex[i][0];
			a_pt1[0] = 0.0;// a->m[i*16+8] = 0.0;
			a_pt1[1] = 0.0;// a->m[i*16+9] = 0.0;
			a_pt1[2] = 0.0;// a->m[i*16+10] = 0.0;
			a_pt1[3] = (double) world_pti[0];// a->m[i*16+11] = world[i][0];
			a_pt1[4] = (double) world_pti[1];// a->m[i*16+12] = world[i][1];
			a_pt1[5] = 1.0;// a->m[i*16+13] = 1.0;
			a_pt1[6] = (double) (-world_pti[0] * i_vertex[i].y);// a->m[i*16+14]=-world[i][0]*vertex[i][1];
			a_pt1[7] = (double) (-world_pti[1] * i_vertex[i].y);// a->m[i*16+15]=-world[i][1]*vertex[i][1];
			b_array[i * 2 + 0][0] = (double) i_vertex[i].x;// b->m[i*2+0] =vertex[i][0];
			b_array[i * 2 + 1][0] = (double) i_vertex[i].y;// b->m[i*2+1] =vertex[i][1];
		}
		if (!a.inverse()) {
			return false;
		}

		o_para.mul(a, b);
		return true;
	}
	/**
	 * この関数は、ラスタのi_vertexsで定義される四角形からパターンを取得して、インスタンスに格納します。
	 */
	public boolean pickFromRaster(INyARRgbRaster image,NyARIntPoint2d[] i_vertexs)
	{
		// パターンの切り出しに失敗することもある。
		NyARMat cpara = new NyARMat(8, 1);
		if (!get_cpara(i_vertexs, cpara)) {
			return false;
		}
		final double[][] para=cpara.getArray();
		final double para00=para[0*3+0][0];
		final double para01=para[0*3+1][0];
		final double para02=para[0*3+2][0];
		final double para10=para[1*3+0][0];
		final double para11=para[1*3+1][0];
		final double para12=para[1*3+2][0];
		final double para20=para[2*3+0][0];
		final double para21=para[2*3+1][0];
		final double para22=1.0;
		
		int lx1 = (int) ((i_vertexs[0].x - i_vertexs[1].x) * (i_vertexs[0].x - i_vertexs[1].x) + (i_vertexs[0].y - i_vertexs[1].y)* (i_vertexs[0].y - i_vertexs[1].y));
		int lx2 = (int) ((i_vertexs[2].x - i_vertexs[3].x) * (i_vertexs[2].x - i_vertexs[3].x) + (i_vertexs[2].y - i_vertexs[3].y)* (i_vertexs[2].y - i_vertexs[3].y));
		int ly1 = (int) ((i_vertexs[1].x - i_vertexs[2].x) * (i_vertexs[1].x - i_vertexs[2].x) + (i_vertexs[1].y - i_vertexs[2].y)* (i_vertexs[1].y - i_vertexs[2].y));
		int ly2 = (int) ((i_vertexs[3].x - i_vertexs[0].x) * (i_vertexs[3].x - i_vertexs[0].x) + (i_vertexs[3].y - i_vertexs[0].y)* (i_vertexs[3].y - i_vertexs[0].y));
		if (lx2 > lx1) {
			lx1 = lx2;
		}
		if (ly2 > ly1) {
			ly1 = ly2;
		}
		
		int sample_pixel_x = this._size.w;
		int sample_pixel_y = this._size.h;
		while (sample_pixel_x * sample_pixel_x < lx1 / 4) {
			sample_pixel_x *= 2;
		}
		while (sample_pixel_y * sample_pixel_y < ly1 / 4) {
			sample_pixel_y *= 2;
		}

		if (sample_pixel_x > AR_PATT_SAMPLE_NUM) {
			sample_pixel_x = AR_PATT_SAMPLE_NUM;
		}
		if (sample_pixel_y > AR_PATT_SAMPLE_NUM) {
			sample_pixel_y = AR_PATT_SAMPLE_NUM;
		}

		final int xdiv = sample_pixel_x / this._size.w;// xdiv = xdiv2/Config.AR_PATT_SIZE_X;
		final int ydiv = sample_pixel_y / this._size.h;// ydiv = ydiv2/Config.AR_PATT_SIZE_Y;


		int img_x = image.getWidth();
		int img_y = image.getHeight();

		final double xdiv2_reciprocal = 1.0 / sample_pixel_x;
		final double ydiv2_reciprocal = 1.0 / sample_pixel_y;
		int r,g,b;
		int[] rgb_tmp = new int[3];

		//ピクセルリーダーを取得
		final int xdiv_x_ydiv = xdiv * ydiv;

		for(int iy=0;iy<this._size.h;iy++){
			for(int ix=0;ix<this._size.w;ix++){
				r=g=b=0;
				//1ピクセルを作成
				for(int j=0;j<ydiv;j++){
					final double yw = 102.5 + 5.0 * (iy*ydiv+j + 0.5) * ydiv2_reciprocal;						
					for(int i=0;i<xdiv;i++){
						final double xw = 102.5 + 5.0 * (ix*xdiv+i + 0.5) * xdiv2_reciprocal;
						final double d = para20 * xw + para21 * yw+ para22;
						if (d == 0) {
							throw new NyARRuntimeException();
						}
						final int xc = (int) ((para00 * xw + para01 * yw + para02) / d);
						final int yc = (int) ((para10 * xw + para11 * yw + para12) / d);
	
						if (xc >= 0 && xc < img_x && yc >= 0 && yc < img_y) {
							image.getPixel(xc, yc, rgb_tmp);
							r += rgb_tmp[0];// R
							g += rgb_tmp[1];// G
							b += rgb_tmp[2];// B
							// System.out.println(xc+":"+yc+":"+rgb_tmp[0]+":"+rgb_tmp[1]+":"+rgb_tmp[2]);
						}
					}
				}
				this._buf[iy*this._size.w+ix]=(((r / xdiv_x_ydiv)&0xff)<<16)|(((g / xdiv_x_ydiv)&0xff)<<8)|(((b / xdiv_x_ydiv)&0xff));
			}
		}
		return true;
	}
	public Object createInterface(Class<?> iIid) 
	{
		if(iIid==INyARPerspectiveCopy.class){
			return NyARPerspectiveCopyFactory.createDriver(this);
		}
		return super.createInterface(iIid);
	}
	
}