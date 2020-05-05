package bowling.domain.frame;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import bowling.domain.PinCount;
import bowling.domain.Round;
import bowling.domain.Score;
import bowling.exception.CannotBowlException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class NormalFrameNodeTest {

  @ParameterizedTest
  @CsvSource(value = {"0,6", "3,5", "3,0"})
  public void testMiss(String firstTrial, String secondTrial) throws CannotBowlException {
    NormalFrameNode normalFrame = NormalFrameNode.initialize();
    int first = Integer.parseInt(firstTrial);
    int second = Integer.parseInt(secondTrial);
    int score = first + second;

    normalFrame.roll(first);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    normalFrame.roll(second);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
    assertThatThrownBy(() -> normalFrame.roll(3))
        .isInstanceOf(CannotBowlException.class);
  }

  @ParameterizedTest
  @CsvSource(value = {"0,10,3", "3,7,10"})
  public void testSpare(String firstTrial, String secondTrial, String thirdTrial)
      throws CannotBowlException
  {
    NormalFrameNode normalFrame = NormalFrameNode.initialize();
    int first = Integer.parseInt(firstTrial);
    int second = Integer.parseInt(secondTrial);
    int third = Integer.parseInt(thirdTrial);
    int score = first + second + third;

    normalFrame.roll(first);
    FrameNode newFrame = normalFrame.roll(second);
    assertThatThrownBy(() -> normalFrame.roll(3))
        .isInstanceOf(CannotBowlException.class);

    newFrame.roll(third);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
  }

  @ParameterizedTest
  @CsvSource(value = {"0,10", "3,5", "6,4"})
  public void testMAX_PIN(String firstTrial, String secondTrial) throws CannotBowlException {
    NormalFrameNode normalFrame = NormalFrameNode.initialize();
    int first = Integer.parseInt(firstTrial);
    int second = Integer.parseInt(secondTrial);
    int score = first + second + PinCount.MAX_PIN;

    FrameNode newFrame = normalFrame.roll(PinCount.MAX_PIN);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());
    assertThatThrownBy(() -> normalFrame.roll(3))
        .isInstanceOf(CannotBowlException.class);

    newFrame.roll(first);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    newFrame.roll(second);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 3, 10})
  public void testDouble(int pinCount) throws CannotBowlException {
    NormalFrameNode normalFrame = NormalFrameNode.initialize();
    int score = PinCount.MAX_PIN + PinCount.MAX_PIN + pinCount;
    FrameNode newFrame = normalFrame.roll(PinCount.MAX_PIN);

    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());
    assertThatThrownBy(() -> normalFrame.roll(3))
        .isInstanceOf(CannotBowlException.class);

    newFrame = newFrame.roll(PinCount.MAX_PIN);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    newFrame.roll(pinCount);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
  }

  @ParameterizedTest
  @CsvSource(value = {"0,10", "3,5", "6,4"})
  public void testMAX_PINBeforeFinalRound(String firstTrial, String secondTrial)
      throws CannotBowlException
  {
    NormalFrameNode normalFrame = NormalFrameNode.of(Round.of(Round.FINAL_ROUND - Round.ROUND_UNIT));
    int first = Integer.parseInt(firstTrial);
    int second = Integer.parseInt(secondTrial);
    int score = first + second + PinCount.MAX_PIN;

    FrameNode newFrame = normalFrame.roll(PinCount.MAX_PIN);
    assertThat(newFrame instanceof FinalFrameNode).isTrue();
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    newFrame.roll(first);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    newFrame.roll(second);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
  }

  @ParameterizedTest
  @CsvSource(value = {"0,10,3", "3,7,10", "3,7,0", "6,4,5"})
  public void testSpareBeforeFinalRound(String firstTrial, String secondTrial, String thirdTrial)
      throws CannotBowlException
  {
    NormalFrameNode normalFrame = NormalFrameNode.of(Round.of(Round.FINAL_ROUND - Round.ROUND_UNIT));
    int first = Integer.parseInt(firstTrial);
    int second = Integer.parseInt(secondTrial);
    int third = Integer.parseInt(thirdTrial);
    int score = first + second + third;

    normalFrame.roll(first);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    FrameNode newFrame = normalFrame.roll(second);
    assertThat(newFrame instanceof FinalFrameNode).isTrue();
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.ofNull());

    newFrame.roll(third);
    assertThat(normalFrame.calculateScore()).isEqualTo(Score.of(score));
  }
}