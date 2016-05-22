library("tm")
temp = read.csv("NLP/verbform.txt", sep="\t")
verbform <- as.matrix(temp)
col.name <- c("id", "base", "past", "participle", "singular", "continuous")
row.name <- verbform[,2]
rownames(verbform) <- row.name

# Getting POS of a Sentence
posfile <- readLines("F:\\Java Workspace\\NLP\\POSTagging\\iofiles\\output.txt")
posfile <- paste(posfile, collapse = ' ')
taggedWord <- unlist(strsplit(posfile, split = " "))
annotatedMatrix <- matrix(ncol = 2, nrow= length(taggedWord))
colnames(annotatedMatrix) <- c("word", "POS")
for(i in 1:length(taggedWord)){
  line <- unlist(strsplit(taggedWord[i], split = "/"))
  annotatedMatrix[i,1] <- line[1]
  annotatedMatrix[i,2] <- line[2]
}
bigramMatrix <- matrix(ncol = 2, nrow= length(taggedWord))
for(i in 1: nrow(annotatedMatrix)-1){
  bigramMatrix[i, 1] <- annotatedMatrix[i, 2]
  bigramMatrix[i, 2] <- annotatedMatrix[i+1, 2]
}

bigramMatrix[is.na(bigramMatrix)] <- "NONE"

findBigramProb <- function(bigram){
  pos <- unlist(strsplit(bigram, split = " "))
  countBigram <- 0;
  countUnigram <- 0;
  for(i in 1:nrow(bigramMatrix)){
    pos1 <- bigramMatrix[i,1]
    pos2 <- bigramMatrix[i,2]
    if(pos1 == pos[1] && pos2 == pos[2]){
      countBigram <- countBigram+1
    }
    if(pos1 == pos[1] || pos2 == pos[1]){
      countUnigram <- countUnigram+1
    }
  }
  countUnigram <- countUnigram/2
  likelihood <- countBigram/countUnigram
  likelihood
}
