require(plyr)
require(ggplot2)
require(gridExtra)

setwd("~/Work/clj-bandit/tmp")

results <- read.csv(file="./results-n1500-t250.csv", header=F)
#results <- read.csv(file="./results.csv", header=F)

names(results) <- c("algo.name", "algo.variant", "simulation.number", "t", "chosen.arm", "reward", "cumulative.reward")
results$algo.variant <- as.factor(results$algo.variant)
results$algo.name <- as.factor(results$algo.name)
results$t <- as.numeric(results$t)

stats.average.reward <- ddply(results, c("algo.name", "algo.variant", "t"), function(df) {mean(df$reward)})
stats.average.reward.plot <- ggplot(stats.average.reward, aes(x = t, y = V1, color = algo.variant)) +
  facet_wrap(~ algo.name) + geom_line() + xlab("Time (Iteration No.)") + ylab("Average Reward") + ggtitle("Average Reward") + ylim(c(0, 1))

stats.probability <- ddply(results, c("algo.name", "algo.variant", "t"), function(df) {mean(df$chosen.arm == ":arm5")})
stats.probability.plot <- ggplot(stats.probability, aes(x = t, y = V1, color = algo.variant)) +
  facet_wrap(~ algo.name) + geom_line() + ylim(c(0, 1)) + xlab("Time (Iteration No.)") + ylab("Pr(Arm=5)") + ggtitle("Accuracy")

stats.cumulative.reward <- ddply(results, c("algo.name", "algo.variant", "t"), function(df) {mean(df$cumulative.reward)})
stats.cumulative.reward.plot <- ggplot(stats.cumulative.reward, aes(x = t, y = V1, color = algo.variant)) +
  facet_wrap(~ algo.name) + geom_line() + xlab("Time (Iteration No.)") + ylab("Cumulative Reward") + ggtitle("Cumulative Reward Performance")

# lets plot the best algorithms on a single cumulative plot
stats.cumulative.reward$algo.name <- as.factor(stats.cumulative.reward$algo.name)
stats.cumulative.reward$algo.variant <- as.factor(stats.cumulative.reward$algo.variant)
algoName <- function(name, variant) {
  paste(name, variant)
}
stats.cumulative.reward.maxes <- ddply(stats.cumulative.reward, c("algo.name", "algo.variant"), function(df) {max(df$V1)})
stats.cumulative.reward.maxes$algo.label <- algoName(stats.cumulative.reward.maxes$algo.name, stats.cumulative.reward.maxes$algo.variant)
stats.cumulative.reward.maxes.plot <- ggplot(stats.cumulative.reward.maxes, aes(x=algo.label, y=V1, fill=algo.name)) +
  geom_bar() + ylab("Cumulative Reward") + xlab("Algorithm Parameter") + ggtitle("Maximum Reward") + scale_x_discrete(labels=stats.cumulative.reward.maxes$algo.variant)
stats.cumulative.reward.maxes.plot

# This should use the numbers from iteration/horizon 250. That is, the results of all simulations
# at that time.
stats.cumulative.final <- subset(results, t == 250, select=c("algo.name", "algo.variant", "cumulative.reward"))
stats.cumulative.final$algo.label <- algoName(stats.cumulative.final$algo.name, stats.cumulative.final$algo.variant)

stats.cumulative.final.boxplot <- ggplot(stats.cumulative.final, aes(algo.label, cumulative.reward)) +
  geom_boxplot() + xlab("Algorithm") + ylab("Reward") + ggtitle("Algorithm Reward at t=250") + theme(axis.text.x = element_text(angle = 90, hjust = 1))
stats.cumulative.final.boxplot

grid.arrange(stats.cumulative.reward.maxes.plot, stats.cumulative.reward.boxplot)

# view individually
stats.average.reward.plot
stats.probability.plot
stats.cumulative.reward.plot

# or as a grid
grid.arrange(stats.average.reward.plot, stats.probability.plot, stats.cumulative.reward.plot)
