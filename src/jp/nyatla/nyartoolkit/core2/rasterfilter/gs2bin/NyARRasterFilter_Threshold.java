/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2008-2009 Ryo Iizuka
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp> or <nyatla(at)nyatla.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core2.rasterfilter.gs2bin;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.raster.*;
import jp.nyatla.nyartoolkit.core.rasterfilter.INyARRasterFilter_GsToBin;
import jp.nyatla.nyartoolkit.core.types.*;

/**
 * 定数閾値による2値化をする。
 * 
 */
public class NyARRasterFilter_Threshold implements INyARRasterFilter_GsToBin
{
	private int _threshold;

	public NyARRasterFilter_Threshold(int i_threshold)
	{
		this._threshold = i_threshold;
		return;
	}

	public void doFilter(NyARGrayscaleRaster i_input, NyARBinRaster i_output) throws NyARException
	{
		assert (i_input.getSize().isEqualSize(i_output.getSize()) == true);

		final int[] out_buf = (int[]) i_output.getBufferReader().getBuffer();
		final int[] in_buf = (int[]) i_input.getBufferReader().getBuffer();

		int bp = 0;
		NyARIntSize size = i_output.getSize();
		for (int y = 0; y < size.h - 1; y++) {
			for (int x = 0; x < size.w; x++) {
				out_buf[y*size.w+x] = in_buf[y*size.w+x] >= this._threshold ? 1 : 0;
				bp += 3;
			}
		}
		return;
	}
	public void setThreshold(int i_threshold)
	{
		this._threshold = i_threshold;
		return;		
	}
}
