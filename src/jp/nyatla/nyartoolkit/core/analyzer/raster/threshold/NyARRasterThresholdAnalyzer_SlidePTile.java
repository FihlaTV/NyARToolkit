package jp.nyatla.nyartoolkit.core.analyzer.raster.threshold;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.analyzer.histgram.*;
import jp.nyatla.nyartoolkit.core.analyzer.raster.*;
import jp.nyatla.nyartoolkit.core.raster.INyARRaster;
import jp.nyatla.nyartoolkit.core.types.NyARHistgram;
/**
 * 明点と暗点をPタイル法で検出して、その中央値を閾値とする。
 * 
 * 
 */
public class NyARRasterThresholdAnalyzer_SlidePTile implements INyARRasterThresholdAnalyzer
{
	private NyARRasterAnalyzer_Histgram _raster_analyzer;
	private NyARHistgramAnalyzer_SlidePTile _sptile;
	private NyARHistgram _histgram;
	public void setVerticalInterval(int i_step)
	{
		this._raster_analyzer.setVerticalInterval(i_step);
		return;
	}
	public NyARRasterThresholdAnalyzer_SlidePTile(int i_persentage,int i_raster_format,int i_vertical_interval) throws NyARException
	{
		assert (0 <= i_persentage && i_persentage <= 50);
		//初期化
		this._sptile=new NyARHistgramAnalyzer_SlidePTile(i_persentage);
		this._histgram=new NyARHistgram(256);
		this._raster_analyzer=new NyARRasterAnalyzer_Histgram(i_raster_format,i_vertical_interval);
	}
	
	public int analyzeRaster(INyARRaster i_input) throws NyARException
	{
		this._raster_analyzer.analyzeRaster(i_input, this._histgram);
		return this._sptile.getThreshold(this._histgram);
	}
}