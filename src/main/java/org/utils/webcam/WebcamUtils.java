package org.utils.webcam;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamCompositeDriver;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

public class WebcamUtils
{

	private static WebcamUtils inst;
	public static final Dimension defaultDimension=new Dimension(640,480);


	public static WebcamUtils inst()
	{
		if(inst==null)
			inst=new WebcamUtils();

		return inst;
	}


	private WebcamUtils()
	{
		Webcam.setDriver(new CompositeDriver());
	}

	public List<Webcam> listWebcam()
	{

		List<Webcam> pcams= new ArrayList<>();
		for(Webcam cam:Webcam.getWebcams())
			pcams.add(cam);

		pcams.add(new SimulatedWebcam());

		return pcams;
	}


	public void changeResolution(Dimension d , Webcam c)
	{
			c.setViewSize(d);

	}

	public boolean registerIPCam(String name, String address, IpCamMode mode)
	{
		try {
				IpCamDeviceRegistry.register(new IpCamDevice(name,address,mode));
				return true;
		} catch (MalformedURLException _) {
			return false;
		}
	}


	class CompositeDriver extends WebcamCompositeDriver {
		public CompositeDriver() {
			add(new WebcamDefaultDriver());
			add(new IpCamDriver());
		}
	}

}
