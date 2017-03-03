package easytests.entities;

import java.util.List;

/**
 * @author malinink
 */
public interface PointInterface extends EntityInterface {
    PointInterface setId(Integer id);

    String getText();

    PointInterface setText(String text);

    String getType();

    PointInterface setType(String type);

    QuizInterface getQuiz();

    PointInterface setQuiz(QuizInterface quiz);

    List<SolutionInterface> getSolutions();

    PointInterface setSolutions(List<SolutionInterface> solutions);
}
