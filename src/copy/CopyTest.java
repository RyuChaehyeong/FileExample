package copy;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyTest {
	
	String beforeCopy = "C:\\BeforeChae\\";
	String afterPaste = "C:\\AfterChae\\";
	String deleteFolder = "C:\\DeleteChae\\";
	
	public static void main(String[] args) throws IOException {
		
		 CopyTest test = new CopyTest();
		 
		 test.copy();
		 

		
	}

	private void copy() throws IOException {
		Path before_folder = Paths.get(this.beforeCopy);
		
		//beforeCopy 경로 안에 있는 폴더 중 that이라는 이름이 들어간 폴더를 거르고 stream에 담음
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(before_folder,
				p -> (p.getFileName().toString().indexOf("that") == -1))) {
			
			for (Path p : stream) {
				Path folder = p.getFileName();	
				String folderName = folder.getFileName().toString();
				System.out.println("folderName: " + folderName);
				
				
				String before_copy = this.beforeCopy + folderName + "\\";		//	 C:\BeforeChae\this1\
				String after_paste = this.afterPaste + folderName + "\\";		//	 C:\AfterChae\this1\
				String delete_folder = this.deleteFolder + folderName + "\\";	//	C:\DeleteChae\this1\
				
				System.out.println("before_copy: " + before_copy);
				System.out.println("after_paste: " + after_paste);
				System.out.println("delete_folder: " + delete_folder );
				
				Path beforeCopy = Paths.get(before_copy);
				System.out.println("Path Of beforeCopy: " + beforeCopy);		// 	 C:\BeforeChae\this1
				
				try (DirectoryStream<Path> text_stream = Files.newDirectoryStream(beforeCopy)) {
					for (Path textFileFolder : text_stream) {
						
						String textFolderName = textFileFolder.getFileName().toString();
						System.out.println("textFolderName: " + textFolderName);	//	 textFolderName: air
						
						Path afterPaste = Paths.get(after_paste + textFolderName);
						System.out.println("Path of afterPaste: " + afterPaste);	//	 C:\AfterChae\this1\air
						
						//beforeCopy의 folder의 this1, this2, this3에 있는 textFileFolder들을 afterPaste의 this1, this2, this3 폴더 안에 폴더 째로 paste
						//C:AfterChae folder에 this1, this2, this3 폴더가 없으면 java.nio.file.NoSuchFileException
						copyFolderNio(textFileFolder, afterPaste, StandardCopyOption.REPLACE_EXISTING);
						
						
						Path delete = Paths.get(delete_folder + textFolderName);
						System.out.println("Path of delete_folder:" + delete_folder + textFolderName); 	//Path of delete_folder:C:\DeleteChae\this1\air
						System.out.println("deletePath: " + delete.getFileName());
						if (Files.exists(delete)) {
							System.out.println("들어옴");
							deleteFile(delete); //air 폴더를 통째로 날림
							System.out.println("delete complete");
						}
					}
				}
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}
			
		}
		
	}
	
	private boolean copyFolderNio(Path sourcePath, Path targetPath, CopyOption copyOption) {
		boolean result = true;

		try {
			Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path target = targetPath.resolve(sourcePath.relativize(dir));
					if (!Files.exists(target)) {
						Files.createDirectory(target);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(final Path dir, final BasicFileAttributes attrs) throws IOException {
					Files.copy(dir, targetPath.resolve(sourcePath.relativize(dir)), copyOption);
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}
	
	
	 public synchronized boolean deleteFile(Path dir) {
	    	boolean result = true;
	        try
	        {
	            Files.walkFileTree(dir, new SimpleFileVisitor<Path>()
	            {
	                  @Override
	                  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                          throws IOException
	                  {
	                      Files.deleteIfExists(file);
	                      return FileVisitResult.CONTINUE;
	                  }
	              
	                  @Override
	                  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
	                  {
	                      if (exc == null) {
	                          Files.deleteIfExists(dir);
	                          return FileVisitResult.CONTINUE;
	                      } else {
	                          throw exc;
	                      }
	                   }
	  
	                });
	        }
	        catch (IOException e)
	        {
	          e.printStackTrace();
	          result = false;
	        }
	        
	        return result;
	    }
		
}
