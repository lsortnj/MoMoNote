package mo.pay.post_it.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipUtil
{
	public static String getContentXMLFile(String zipFilePath,
			String targetFolder) throws IOException
	{
		File zipDir = new File(targetFolder);
		String xml_file = "";

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;

		ZipFile zipfile;
		ZipEntry entry;

		int count;
		byte data[] = new byte[8192];

		deleteFolder(zipDir);

		zipfile = new ZipFile(new File(zipFilePath),"BIG5",false);

		Enumeration<?> e = zipfile.getEntries();

		while (e.hasMoreElements())
		{
			entry = (ZipEntry) e.nextElement();

			String entry_name = entry.getName();

			if (entry_name.equals("content.xml"))
			{
				bis = new BufferedInputStream(zipfile.getInputStream(entry));
				fos = new FileOutputStream(targetFolder + entry_name);
				bos = new BufferedOutputStream(fos, 8192);
				while ((count = bis.read(data, 0, 8192)) != -1)
				{
					bos.write(data, 0, count);
				}
				bos.flush();
				bos.close();
				bis.close();

				xml_file = targetFolder + entry_name;
			}

		}


		return xml_file;
	}

	public static boolean unzipFile(String zipFilePath, String targetFolder)
			throws IOException
	{
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;

		ZipFile zipfile;
		ZipEntry entry;

		int count;
		byte data[] = new byte[8192];

		new File(targetFolder).mkdir();
		
		zipfile = new ZipFile(new File(zipFilePath),"BIG5",false);

		Enumeration<?> e = zipfile.getEntries();

		while (e.hasMoreElements())
		{
			entry = (ZipEntry) e.nextElement();

			// String entry_name = URLEncoder.encode(entry.getName(),"UTF-8");

			String entry_name = entry.getName();

			if (entry.getName().contains("/"))
			{
				// is a directory

				String[] folders = entry.getName().split("/");

				String current_path = targetFolder;

				for (int i = 0; i < folders.length; i++)
				{
					if (folders[i].contains("."))
					{
						continue;
					}

					new File(current_path + "/" + folders[i]).mkdir();

					current_path += "/" + folders[i];
				}
			}

			if (!entry_name.endsWith("/"))
			{
				bis = new BufferedInputStream(zipfile.getInputStream(entry));
				fos = new FileOutputStream(targetFolder + "/" + entry_name);
				bos = new BufferedOutputStream(fos, 8192);

				while ((count = bis.read(data, 0, 8192)) != -1)
				{
					bos.write(data, 0, count);
				}
				bos.flush();
				bos.close();
				bis.close();
			}
		}

		return true;
	}

	public static boolean unzipExcludeSpecificFiles(String zipFilePath,
			String targetFolder, ArrayList<String> excludeFileName)
	{
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;

		ZipFile zipfile;
		ZipEntry entry;

		int count;
		byte data[] = new byte[8192];

		new File(targetFolder).mkdir();
		
		// deleteFolder(zipDir);

		try
		{
			zipfile = new ZipFile(new File(zipFilePath),"BIG5",false);

			Enumeration<?> e = zipfile.getEntries();

			while (e.hasMoreElements())
			{
				entry = (ZipEntry) e.nextElement();

				String entry_name = entry.getName();
				;

				if (!excludeFileName.contains(entry_name))
				{
					bis = new BufferedInputStream(zipfile.getInputStream(entry));
					fos = new FileOutputStream(targetFolder + entry_name);
					bos = new BufferedOutputStream(fos, 8192);
					while ((count = bis.read(data, 0, 8192)) != -1)
					{
						bos.write(data, 0, count);
					}
					bos.flush();
					bos.close();
					bis.close();
				}
			}

			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	public static boolean unzipSpecificFiles(String zipFilePath,
			String targetFolder, List<String> specificFileName)
	{
		// File zipDir=new File( specificTempFolder );

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;

		ZipFile zipfile;
		ZipEntry entry;

		int count;
		byte data[] = new byte[8192];

		new File(targetFolder).mkdir();
		
		// deleteFolder(zipDir);

		try
		{
			zipfile = new ZipFile(new File(zipFilePath),"BIG5",false);

			Enumeration<?> e = zipfile.getEntries();

			while (e.hasMoreElements())
			{
				entry = (ZipEntry) e.nextElement();

				String entry_name = entry.getName();
				;

				if (specificFileName.contains(entry_name))
				{
					bis = new BufferedInputStream(zipfile.getInputStream(entry));
					fos = new FileOutputStream(targetFolder + entry_name);
					bos = new BufferedOutputStream(fos, 8192);
					while ((count = bis.read(data, 0, 8192)) != -1)
					{
						bos.write(data, 0, count);
					}
					bos.flush();
					bos.close();
					bis.close();
				}
			}

			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
	
	public static boolean unzipSpecificFile(String zipFilePath,
			String targetFolder, String specificFileName)
	{
		// File zipDir=new File( specificTempFolder );

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;

		ZipFile zipfile;
		ZipEntry entry;

		int count;
		byte data[] = new byte[8192];

		// deleteFolder(zipDir);

		try
		{
			zipfile = new ZipFile(new File(zipFilePath),"BIG5",false);

			Enumeration<?> e = zipfile.getEntries();

			while (e.hasMoreElements())
			{
				entry = (ZipEntry) e.nextElement();

				String entry_name = entry.getName();
				

				if (specificFileName.equals(entry_name))
				{
					bis = new BufferedInputStream(zipfile.getInputStream(entry));
					fos = new FileOutputStream(targetFolder + entry_name);
					bos = new BufferedOutputStream(fos, 8192);
					while ((count = bis.read(data, 0, 8192)) != -1)
					{
						bos.write(data, 0, count);
					}
					bos.flush();
					bos.close();
					bis.close();
					
					break;
				}
			}

			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	private static boolean deleteFolder(File folder)
	{
		try
		{
			if (folder.isDirectory())
			{
				File[] tempfiles = folder.listFiles();

				for (int i = 0; i < tempfiles.length; i++)
				{
					tempfiles[i].delete();
				}
			} else
			{
				folder.mkdir();
			}

			return true;

		} catch (Exception e)
		{
			return false;
		}
	}
}
