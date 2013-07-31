package components

import scala.swing.Component
import javax.swing.JEditorPane

/**
 * author mikwie
 *
 */
class EditorPane extends Component {

  override lazy val peer = new JEditorPane()

  def contentType_=(contentType: String) = peer.setContentType(contentType)

  def contentType = peer.getContentType

  def text_=(text: String) = peer.setText(text)

  def text = peer.getText

}
