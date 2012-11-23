require(plyr)
require(ggplot2)

setwd("~/Work/clj-bandit/tmp")

results <- read.csv(file="./results-n1500-t250.csv", header=F)
#results <- read.csv(file="./results.csv", header=F)

names(results) <- c("algo.name", "algo.variant", "algo.parameter", "simulation.number", "t", "chosen.arm", "reward", "cumulative.reward")
results$algo.variant <- as.factor(results$algo.variant)
results$algo.parameter <- as.factor(results$algo.parameter)
results$algo.name <- as.factor(results$algo.name)
results$t <- as.numeric(results$t)

stats.average.reward <- ddply(results, c("algo.name", "algo.variant", "algo.parameter", "t"), function(df) {mean(df$reward)})
stats.average.reward.plot <- ggplot(stats.average.reward, aes(x = t, y = V1, color = algo.parameter)) +
  facet_wrap(~ algo.name + algo.variant) + geom_line() + xlab("Time (Iteration No.)") + ylab("Average Reward") + ggtitle("Average Reward") + ylim(c(0, 1))

ggsave(stats.average.reward.plot, file="average_reward.png")

stats.probability <- ddply(results, c("algo.name", "algo.variant", "algo.parameter", "t"), function(df) {mean(df$chosen.arm == ":arm5")})
stats.probability.plot <- ggplot(stats.probability, aes(x = t, y = V1, color = algo.parameter)) +
  facet_wrap(~ algo.name + algo.variant) + geom_line() + ylim(c(0, 1)) + xlab("Time (Iteration No.)") + ylab("Pr(Arm=5)") + ggtitle("Accuracy")

ggsave(stats.probability.plot, file="accuracy.png")

stats.cumulative.reward <- ddply(results, c("algo.name", "algo.variant", "algo.parameter", "t"), function(df) {mean(df$cumulative.reward)})
stats.cumulative.reward.plot <- ggplot(stats.cumulative.reward, aes(x = t, y = V1, color = algo.parameter)) +
  facet_wrap(~ algo.name + algo.variant) + geom_line() + xlab("Time (Iteration No.)") + ylab("Cumulative Reward") + ggtitle("Cumulative Reward Performance")

ggsave(stats.cumulative.reward.plot, file="cumulative_reward.png")

# This should use the numbers from iteration/horizon 250. That is, the results of all simulations
# at that time.
stats.cumulative.final <- subset(results, t == 250, select=c("algo.name", "algo.variant", "algo.parameter", "cumulative.reward"))
stats.cumulative.final.boxplot <- ggplot(stats.cumulative.final, aes(paste(algo.variant, algo.parameter), cumulative.reward)) +
  facet_wrap(~ algo.name) + geom_boxplot() + xlab("Algorithm") + ylab("Reward") + ggtitle("Algorithm Reward at t=250") + theme(axis.text.x = element_text(angle = 90, hjust = 1))

ggsave(stats.cumulative.final.boxplot, file="cumulative_reward_final_value.png")
