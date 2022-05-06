package plagdetect;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PlagiarismDetector implements IPlagiarismDetector {
	
	private int ngrams;
	private Map<String, Map<String, Integer>> result = new HashMap<>();
	private Map<String, Set<String>> uniqueNgrams = new HashMap<>();
	private Set<String> suspicious = new HashSet<>();
	private Set<String> processedFile = new HashSet<>();
	
	public PlagiarismDetector(int n) {
		// TODO implement this method
		ngrams = n;
	}
	
	@Override
	public int getN() {
		// TODO Auto-generated method stub
		return ngrams;
	}

	@Override
	public Collection<String> getFilenames() {
		// TODO Auto-generated method stub
		return processedFile;
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {
		// TODO Auto-generated method stub
		return uniqueNgrams.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		// TODO Auto-generated method stub
		return getNgramsInFile(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public void readFile(File file) throws IOException {
		// TODO Auto-generated method stub
		// most of your work can happen in this method
		Scanner sc = new Scanner(file);
		String fileName = file.getName();
		Set<String> set1 = new HashSet<>();
		Set<String> set2 = new HashSet<>();
		
		//get the unique n-grams in the file
		uniqueNgrams.put(fileName, new HashSet<>());
		while (sc.hasNextLine()) {
			String[] wordInLine = sc.nextLine().split(" ");
			if (wordInLine.length >= getN()) {
				for (int i = 0; i<= wordInLine.length-getN(); i++) {
					int tmp = i;
					String str = "";
					while (tmp-i < getN()) {
						str += wordInLine[tmp] + " ";
						tmp++;
					}
					uniqueNgrams.get(fileName).add(str.trim());
				}
			}
		}
		
		/*for (Map.Entry k : uniqueNgrams.entrySet()) {
			System.out.println("key: "+k.getKey() + " value: "+k.getValue());
		}*/
		
		processedFile.add(fileName);
		result.put(fileName, new HashMap<>());
		set1 = uniqueNgrams.get(fileName);
		for (String f : processedFile) {
			if (!f.equals(fileName)) {
				result.get(fileName).put(f, 0);
				set2 = uniqueNgrams.get(f);
				for (String ngrams : set1) {
					if (set2.contains(ngrams)) {
						result.get(fileName).computeIfPresent(f, (key, val) -> val +1);
					}
				}
			}
		}
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		// TODO Auto-generated method stub
		if (!result.get(file1).containsKey(file2)) {
			if (result.get(file2).containsKey(file1)) {
				return result.get(file2).get(file1);
			}else 
				return 0;
		} 
		return result.get(file1).get(file2);
		
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		for (Map.Entry pair : result.entrySet()) {
			String file1 = (String) pair.getKey().toString();
			for (Map.Entry p : result.get(file1).entrySet()) {
				int val = (int) p.getValue();
				String file2 = p.getKey().toString();
				if (val >= minNgrams) {
					if (file1.compareTo(file2) < 0) {
						String toAdd = file1 + " " + file2 + " " + String.valueOf(val) ;
						suspicious.add(toAdd);
						//suspicious.add(String.format("%file1 %file2 %val", file1, file2, String.valueOf(val)));
					} else if (file1.compareTo(file2) > 0) {
						//suspicious.add(String.format("%file2 %file1 %val", file2, file1, String.valueOf(val)));
						String toAdd = file2 + " " + file1 + " " + String.valueOf(val) ;
						suspicious.add(toAdd);
					}
				}
			}
		}
		for (String str : suspicious) {
			System.out.println("Suspicious:" + str);
		}
		return suspicious;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
	}
	
}
