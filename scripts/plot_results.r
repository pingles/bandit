require(plyr)
require(ggplot2)

setwd("~/Work/clj-bandit/tmp")

results <- read.csv(file="./results.csv", header=F)
names(results) <- c("epsilon.value", "simulation.number", "t", "chosen.arm", "reward", "cumulative.reward")
results$epsilon.value <- as.factor(results$epsilon.value)

stats <- ddply(results, c("epsilon.value", "t"), function(df) {mean(df$reward)})
stats.plot <- ggplot(stats, aes(x = t, y = V1, color = epsilon.value))
stats.plot + geom_line() + ylim(0, 1) + xlab("Iterations") + ylab("Average Reward") + ggtitle("Epsilon-Greedy Performance")

stats.probability <- ddply(results, c("epsilon.value", "t"), function(df) {mean(df$chosen.arm == ":arm5")})
stats.probability.plot <- ggplot(stats.probability, aes(x = t, y = V1, color = epsilon.value))
stats.probability.plot + geom_line() + ylim(c(0, 1)) + xlab("Iterations") + ylab("Pr(Arm=5)") + ggtitle("Epsilon-Greedy Performance")

stats.cumulative.reward <- ddply(results, c("epsilon.value", "t"), function(df) {mean(df$cumulative.reward)})
stats.cumulative.reward.plot <- ggplot(stats.cumulative.reward, aes(x = t, y = V1, color = epsilon.value))
stats.cumulative.reward.plot + geom_line() + xlab("Iterations") + ylab("Cumulative Reward") + ggtitle("Epsilon-Greedy Performance")
